#!/usr/bin/env python3
"""Generate null-input tests for all mappers with <80% branch coverage."""

import os
import re
import glob
import sys

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MAIN_SRC = os.path.join(REPO_ROOT, "src/main/java/com/example/demo")
TEST_SRC = os.path.join(REPO_ROOT, "src/test/java/com/example/demo")

# Excluded from coverage per AGENTS.md
EXCLUDED_PACKAGES = [
    'com.example.demo.client',
    'com.example.demo.model',
    'com.example.demo.api',
    'com.example.demo.invoker',
    'com.example.demo.dto',
    'com.example.demo.config',
    'com.example.demo.Application',
    'com.example.demo.repository.Dao',
    'com.example.demo.service.utils',
]

PACKAGE_MAP = {
    'com.example.demo.endpoint.rest.mapper': 'Service',
    'com.example.demo.endpoint.rest.mapper.core': 'Service.core',
    'com.example.demo.endpoint.rest.mapper.hr': 'Service.hr',
    'com.example.demo.endpoint.rest.mapper.money': 'Service.money',
    'com.example.demo.endpoint.rest.mapper.movement': 'Service.movement',
    'com.example.demo.endpoint.rest.mapper.notification': 'Service.notification',
    'com.example.demo.endpoint.rest.mapper.task': 'Service.task',
}

def is_excluded(pkg_name, class_name):
    full = pkg_name + '.' + class_name
    for excl in EXCLUDED_PACKAGES:
        if full.startswith(excl) or class_name.startswith(excl):
            return True
    return False

def get_test_package(source_pkg):
    """Map from source package to test package."""
    for src_prefix, test_prefix in PACKAGE_MAP.items():
        if source_pkg == src_prefix or source_pkg.startswith(src_prefix + '.'):
            suffix = source_pkg[len(src_prefix):]
            if suffix:
                return 'com.example.demo.' + test_prefix + suffix
            return 'com.example.demo.' + test_prefix
    # Default: replace com.example.demo with Service
    if source_pkg.startswith('com.example.demo.'):
        return 'com.example.demo.Service.' + source_pkg[len('com.example.demo.'):]
    return 'com.example.demo.Service.' + source_pkg

def parse_mapper(filepath):
    """Parse a mapper Java file and extract key info."""
    with open(filepath) as f:
        content = f.read()

    # Get package
    pkg_match = re.search(r'^package\s+([\w.]+);', content, re.MULTILINE)
    if not pkg_match:
        return None
    pkg = pkg_match.group(1)

    # Get class name
    class_match = re.search(r'class\s+(\w+)', content)
    if not class_match:
        return None
    class_name = class_match.group(1)

    # Skip excluded
    if is_excluded(pkg, class_name):
        return None

    # Extract dependencies (private final fields or constructor params)
    # @AllArgsConstructor + private final fields
    field_pattern = re.compile(r'private\s+final\s+(\w+)\s+(\w+);')
    deps = field_pattern.findall(content)

    # Check for @AllArgsConstructor or @RequiredArgsConstructor
    has_lombok = '@AllArgsConstructor' in content or '@RequiredArgsConstructor' in content

    # Find all single-parameter methods
    # Use multi-line matching for methods spanning multiple lines
    all_content_no_comments = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
    all_content_no_comments = re.sub(r'//.*', '', all_content_no_comments)
    
    # Count unique single-param toDomain signatures
    to_domain_params = re.findall(
        r'public\s+\S+(?:<[^>]+>)?\s+toDomain\s*\(\s*(\w+(?:<\w+>)?)\s+\w+\s*\)',
        all_content_no_comments
    )
    # Only generate toDomain test if there's exactly one unique signature
    to_domain_name = ('toDomain' if len(to_domain_params) == 1 else None)
    
    # Find all toRest* methods with single param (toRest, toRestXxx)
    to_rest_methods = re.findall(
        r'public\s+\S+(?:<[^>]+>)?\s+(toRest\w*)\s*\(\s*\w+\s+\w+\s*\)',
        all_content_no_comments
    )
    # Filter to single-arg methods (not lists)
    has_to_rest = any(m.endswith('toRest') for m in to_rest_methods)
    to_rest_name = None
    for m in to_rest_methods:
        if m == 'toRest':
            to_rest_name = 'toRest'
        elif m.startswith('toRest') and m != 'toRest' and m != 'toRestList':
            if to_rest_name is None:
                to_rest_name = m
    if not to_rest_name and len(to_rest_methods) > 0:
        to_rest_name = to_rest_methods[0] if to_rest_methods[0] not in ('toRestList',) else None

    # Find all methods with public return
    method_pattern = re.compile(r'public\s+(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(')
    methods = method_pattern.findall(content)

    if not to_domain_name and not to_rest_name:
        return None

    return {
        'pkg': pkg,
        'class_name': class_name,
        'deps': deps,
        'has_lombok': has_lombok,
        'to_domain_name': to_domain_name,
        'to_rest_name': to_rest_name,
        'methods': methods,
        'source_path': filepath,
    }

def generate_test(info):
    """Generate test file content for a mapper."""
    class_name = info['class_name']
    test_pkg = get_test_package(info['pkg'])

    lines = []
    lines.append(f"package {test_pkg};")
    lines.append("")
    lines.append("import static org.assertj.core.api.Assertions.assertThat;")
    lines.append("")
    lines.append(f"import {info['pkg']}.{class_name};")
    lines.append("import org.junit.jupiter.api.Test;")
    lines.append("import org.junit.jupiter.api.extension.ExtendWith;")
    lines.append("import org.mockito.InjectMocks;")
    lines.append("import org.mockito.Mock;")
    lines.append("import org.mockito.junit.jupiter.MockitoExtension;")

    # Add mock imports for dependencies
    dep_types = set()
    for dep_type, dep_name in info['deps']:
        dep_types.add(dep_type)
    # Try to find imports for dependency types
    with open(info['source_path']) as f:
        src_content = f.read()
    for dt in dep_types:
        import_pattern = re.compile(r'^import\s+([\w.]+\.' + re.escape(dt) + r');', re.MULTILINE)
        m = import_pattern.search(src_content)
        if m:
            imp = m.group(1)
            if not any(imp.startswith(excl) for excl in EXCLUDED_PACKAGES):
                lines.append(f"import {imp};")
        else:
            # Same package as the mapper itself
            lines.append(f"import {info['pkg']}.{dt};")

    lines.append("")
    lines.append("@ExtendWith(MockitoExtension.class)")
    lines.append(f"class {class_name}Test {{")
    lines.append("")

    # Add mock fields
    for dep_type, dep_name in info['deps']:
        lines.append(f"  @Mock private {dep_type} {dep_name};")
    lines.append(f"  @InjectMocks private {class_name} mapper;")
    lines.append("")

    # toDomain null test
    if info['to_domain_name']:
        lines.append("  @Test")
        lines.append(f"  void toDomain_null_returnsNull() {{")
        lines.append(f"    assertThat(mapper.toDomain(null)).isNull();")
        lines.append("  }")
        lines.append("")

    # toRest null test
    to_rest_name = info['to_rest_name']
    if to_rest_name:
        lines.append("  @Test")
        lines.append(f"  void {to_rest_name}_null_returnsNull() {{")
        lines.append(f"    assertThat(mapper.{to_rest_name}(null)).isNull();")
        lines.append("  }")
        lines.append("")

    lines.append("}")
    lines.append("")
    return '\n'.join(lines)

def main():
    mapper_files = glob.glob(f"{MAIN_SRC}/endpoint/rest/mapper/**/*.java", recursive=True)
    mapper_files += glob.glob(f"{MAIN_SRC}/endpoint/rest/mapper/*.java", recursive=True)

    # Remove duplicates
    mapper_files = sorted(set(mapper_files))

    generated = 0
    for filepath in mapper_files:
        info = parse_mapper(filepath)
        if not info:
            continue

        # Determine test file path
        test_pkg = get_test_package(info['pkg'])
        # Strip the base package prefix for the file path (TEST_SRC already includes com/example/demo)
        base = 'com.example.demo.'
        if test_pkg.startswith(base):
            test_pkg_rel = test_pkg[len(base):].replace('.', '/')
        else:
            test_pkg_rel = test_pkg.replace('.', '/')
        test_filename = f"{info['class_name']}Test.java"
        test_dir = os.path.join(TEST_SRC, test_pkg_rel)
        test_filepath = os.path.join(test_dir, test_filename)

        # Skip if test already exists
        if os.path.exists(test_filepath):
            print(f"SKIP (exists): {test_filepath}")
            continue

        content = generate_test(info)
        os.makedirs(test_dir, exist_ok=True)
        with open(test_filepath, 'w') as f:
            f.write(content)
        print(f"GEN: {test_filepath}")
        generated += 1

    print(f"\nGenerated {generated} test files")

if __name__ == '__main__':
    main()
