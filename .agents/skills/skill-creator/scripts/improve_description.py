#!/usr/bin/env python3
"""Improve a skill description based on eval results.

Takes eval results (from run_eval.py) and generates an improved description
by calling the model CLI as a subprocess. Configurable with --model-cmd.
"""

import argparse
import json
import os
import re
import shlex
import subprocess
import sys
from pathlib import Path

from scripts.utils import parse_skill_md


def _call_model(
    prompt: str,
    model_cmd: str,
    model: str | None,
    timeout: int = 300,
) -> str:
    """Run the model CLI with prompt on stdin and return the text response.

    Prompt goes over stdin (not argv) because it can easily exceed comfortable
    argv length when embedding the full SKILL.md content.
    """
    cmd_parts = shlex.split(model_cmd)
    if "--output-format" not in model_cmd:
        cmd_parts.append("--output-format")
        cmd_parts.append("text")

    # Remove CLAUDECODE env var to allow nesting inside a Claude Code session
    env = {k: v for k, v in os.environ.items() if k != "CLAUDECODE"}

    result = subprocess.run(
        cmd_parts,
        input=prompt,
        capture_output=True,
        text=True,
        env=env,
        timeout=timeout,
    )
    if result.returncode != 0:
        raise RuntimeError(
            f"Model CLI exited {result.returncode}\nstderr: {result.stderr}"
        )
    return result.stdout


IMPROVEMENT_PROMPT = """You are optimizing the description field of a SKILL.md file.
The description is the primary mechanism by which an AI agent decides whether to
consult this skill. Your task: improve this description so it triggers correctly
for queries that need it and avoids triggering for queries that don't.

Current description:
{current_description}

Full skill content:
```markdown
{skill_content}
```

Eval results:
{eval_results_json}

History of previous descriptions and their scores:
{history_json}

{'Test results (held-out set):\n' + test_results_json if test_results else ''}

Requirements:
1. Return ONLY the new description text, nothing else.
2. The description must be 1-1024 characters.
3. Keep it specific enough for the agent to choose correctly.
4. Include both what the skill does AND specific contexts/phrasings that should trigger it.
5. If certain queries failed to trigger when they should have, add phrasings that cover those cases.
6. If certain queries triggered when they shouldn't have, add clarifying language to narrow the scope.
7. Learn from history: descriptions that scored well share patterns worth keeping.
8. Do NOT include angle brackets (< or >) in the description.
"""


def improve_description(
    skill_name: str,
    skill_content: str,
    current_description: str,
    eval_results: dict,
    history: list[dict],
    model_cmd: str = "claude -p",
    model: str | None = None,
    test_results: dict | None = None,
    log_dir: Path | None = None,
    iteration: int | None = None,
) -> str:
    """Generate an improved description based on eval results."""
    train_results = eval_results.get("results", [])
    test_results_data = test_results.get("results", []) if test_results else []

    # Build eval results JSON for the prompt
    eval_results_json = json.dumps(train_results, indent=2)
    history_json = json.dumps(history, indent=2)
    test_results_json = json.dumps(test_results_data, indent=2) if test_results_data else ""

    prompt = IMPROVEMENT_PROMPT.format(
        current_description=current_description,
        skill_content=skill_content,
        eval_results_json=eval_results_json,
        history_json=history_json,
        test_results_json=test_results_json,
    )

    new_description = _call_model(prompt, model_cmd, model).strip()

    # Strip markdown code fences if the model wrapped the output
    new_description = re.sub(r'^```(?:yaml|markdown)?\n', '', new_description, flags=re.MULTILINE)
    new_description = re.sub(r'\n```$', '', new_description, flags=re.MULTILINE)
    new_description = new_description.strip().strip('"').strip("'")

    if log_dir and iteration is not None:
        log_dir = Path(log_dir)
        log_dir.mkdir(parents=True, exist_ok=True)
        prompt_path = log_dir / f"iteration-{iteration}-prompt.txt"
        prompt_path.write_text(prompt)
        response_path = log_dir / f"iteration-{iteration}-response.txt"
        response_path.write_text(new_description)
        if hasattr(improve_description, '_last_raw_response'):
            raw_path = log_dir / f"iteration-{iteration}-raw.txt"
            raw_path.write_text(str(improve_description._last_raw_response))

    if log_dir and iteration is not None:
        log_dir = Path(log_dir)
        log_dir.mkdir(parents=True, exist_ok=True)
        prompt_path = log_dir / f"iteration-{iteration}-prompt.txt"
        prompt_path.write_text(prompt)
        response_path = log_dir / f"iteration-{iteration}-response.txt"
        response_path.write_text(new_description)

    return new_description


def _strip_markdown_fences(text: str) -> str:
    """Remove markdown code fences from a string."""
    text = re.sub(r'^```(?:yaml|markdown)?\n', '', text, flags=re.MULTILINE)
    text = re.sub(r'\n```$', '', text, flags=re.MULTILINE)
    return text.strip()


def parse_improvement_response(response: str) -> str | None:
    """Parse a model response to extract the improved description."""
    text = response.strip()

    # Try to extract from a YAML code block
    yaml_match = re.search(r'```(?:yaml)\n(.*?)```', text, re.DOTALL)
    if yaml_match:
        desc_match = re.search(r'description:\s*(.*)', yaml_match.group(1))
        if desc_match:
            return desc_match.group(1).strip().strip('"').strip("'")

    # Try to extract from quotes
    for quote_char in ['"', "'"]:
        if text.startswith(quote_char) and text.endswith(quote_char):
            return text.strip(quote_char)

    # Otherwise, use the whole response
    return text


def main():
    parser = argparse.ArgumentParser(description="Improve skill description based on eval results")
    parser.add_argument("--eval-results", required=True, help="Path to eval results JSON from run_eval.py")
    parser.add_argument("--skill-path", required=True, help="Path to skill directory")
    parser.add_argument(
        "--model-cmd",
        default="claude -p",
        help="CLI command to invoke the model (e.g., 'claude -p', 'opencode -p')",
    )
    parser.add_argument("--model", default=None, help="Model to use")
    parser.add_argument("--log-dir", default=None, help="Directory to save prompt/response logs")
    parser.add_argument("--iteration", type=int, default=None, help="Iteration number for logging")
    args = parser.parse_args()

    eval_results = json.loads(Path(args.eval_results).read_text())
    skill_path = Path(args.skill_path)

    name, _, content = parse_skill_md(skill_path)
    current_description = eval_results.get("description", "")

    new_description = improve_description(
        skill_name=name,
        skill_content=content,
        current_description=current_description,
        eval_results=eval_results,
        history=[],
        model_cmd=args.model_cmd,
        model=args.model,
        log_dir=Path(args.log_dir) if args.log_dir else None,
        iteration=args.iteration,
    )

    print(new_description)


if __name__ == "__main__":
    main()
