#!/usr/bin/env python3
"""Run trigger evaluation for a skill description.

Tests whether a skill's description causes the AI agent to trigger (read the skill)
for a set of queries. Supports configurable model CLI and skill injection mechanism.
Outputs results as JSON.
"""

import argparse
import json
import os
import select
import shlex
import subprocess
import sys
import time
import uuid
from concurrent.futures import ProcessPoolExecutor, as_completed
from pathlib import Path

from scripts.utils import parse_skill_md


def find_project_root(marker: str = ".opencode") -> Path:
    """Find the project root by walking up from cwd looking for a marker directory.

    Default marker is .opencode (OpenCode). Can be overridden for other agents.
    Falls back to cwd if no marker found.
    """
    current = Path.cwd()
    for parent in [current, *current.parents]:
        if (parent / marker).is_dir():
            return parent
    return current


def _parse_model_cmd(model_cmd: str) -> list[str]:
    """Parse a model CLI command string into a list of arguments."""
    return shlex.split(model_cmd)


def _detect_trigger_from_stream(
    buffer: str,
    skill_name: str,
) -> bool:
    """Detect skill triggering from stream-format output (Claude stream-json).

    Parses newline-delimited JSON events looking for tool_use events that
    reference the skill by name.
    """
    for line in buffer.split("\n"):
        line = line.strip()
        if not line:
            continue
        try:
            event = json.loads(line)
        except json.JSONDecodeError:
            continue

        # Claude stream-json format
        if event.get("type") == "stream_event":
            se = event.get("event", {})
            se_type = se.get("type", "")
            if se_type == "content_block_start":
                cb = se.get("content_block", {})
                if cb.get("type") == "tool_use":
                    return True  # Agent is using a tool (likely the skill)
            elif se_type == "content_block_delta":
                delta = se.get("delta", {})
                if delta.get("type") == "input_json_delta":
                    partial = delta.get("partial_json", "")
                    if skill_name in partial:
                        return True
            elif se_type == "message_stop":
                return False

        # Full assistant message fallback
        elif event.get("type") == "assistant":
            message = event.get("message", {})
            for content_item in message.get("content", []):
                if content_item.get("type") != "tool_use":
                    continue
                tool_name = content_item.get("name", "")
                tool_input = content_item.get("input", {})
                if tool_name == "Skill" and skill_name in tool_input.get("skill", ""):
                    return True
                if tool_name == "Read" and skill_name in tool_input.get("file_path", ""):
                    return True

        elif event.get("type") == "result":
            return False

    return False


def run_single_query(
    query: str,
    skill_name: str,
    skill_description: str,
    timeout: int,
    project_root: str,
    model_cmd: str = "claude -p",
    skill_inject_dir: str | None = None,
    model: str | None = None,
) -> bool:
    """Run a single query and return whether the skill was triggered.

    Creates a command/skill file in the agent's skill discovery directory,
    then runs the model CLI with the query. Detects triggering from output.
    """
    unique_id = uuid.uuid4().hex[:8]
    clean_name = f"{skill_name}-{unique_id}"

    # Determine where to place the skill file for agent discovery
    if skill_inject_dir:
        inject_dir = Path(skill_inject_dir)
    else:
        # Default: try common agent skill directories
        for candidate in [".opencode/skills", ".claude/commands", ".claude/skills"]:
            p = Path(project_root) / candidate
            if p.is_dir():
                inject_dir = p
                break
        else:
            inject_dir = Path(project_root) / ".opencode" / "skills"

    command_file = inject_dir / f"{clean_name}.md"

    try:
        inject_dir.mkdir(parents=True, exist_ok=True)
        # Write a minimal skill/command file so the agent discovers it
        indented_desc = "\n  ".join(skill_description.split("\n"))
        command_content = (
            f"---\n"
            f"name: {clean_name}\n"
            f"description: |\n"
            f"  {indented_desc}\n"
            f"---\n\n"
            f"# {skill_name}\n\n"
            f"{skill_description}\n"
        )
        command_file.write_text(command_content)

        # Parse the model CLI command
        cmd_parts = _parse_model_cmd(model_cmd)
        # Append the query
        cmd_parts.extend(["-p", query])

        # Remove CLAUDECODE env var to allow nesting
        env = {k: v for k, v in os.environ.items() if k != "CLAUDECODE"}

        process = subprocess.Popen(
            cmd_parts,
            stdout=subprocess.PIPE,
            stderr=subprocess.DEVNULL,
            cwd=project_root,
            env=env,
        )

        triggered = False
        start_time = time.time()
        buffer = ""

        try:
            while time.time() - start_time < timeout:
                if process.poll() is not None:
                    remaining = process.stdout.read()
                    if remaining:
                        buffer += remaining.decode("utf-8", errors="replace")
                    break

                ready, _, _ = select.select([process.stdout], [], [], 1.0)
                if not ready:
                    continue

                chunk = os.read(process.stdout.fileno(), 8192)
                if not chunk:
                    break
                buffer += chunk.decode("utf-8", errors="replace")

                # Try to detect triggering from stream events
                triggered = _detect_trigger_from_stream(buffer, clean_name)
                if triggered:
                    break
        finally:
            if process.poll() is None:
                process.kill()
                process.wait()

        # If stream detection failed, try full output
        if not triggered:
            triggered = clean_name in buffer or skill_name in buffer

        return triggered
    finally:
        if command_file.exists():
            command_file.unlink()


def run_eval(
    eval_set: list[dict],
    skill_name: str,
    description: str,
    num_workers: int,
    timeout: int,
    project_root: Path,
    runs_per_query: int = 1,
    trigger_threshold: float = 0.5,
    model_cmd: str = "claude -p",
    skill_inject_dir: str | None = None,
    model: str | None = None,
) -> dict:
    """Run the full eval set and return results."""
    results = []

    with ProcessPoolExecutor(max_workers=num_workers) as executor:
        future_to_info = {}
        for item in eval_set:
            for run_idx in range(runs_per_query):
                future = executor.submit(
                    run_single_query,
                    item["query"],
                    skill_name,
                    description,
                    timeout,
                    str(project_root),
                    model_cmd,
                    skill_inject_dir,
                    model,
                )
                future_to_info[future] = (item, run_idx)

        query_triggers: dict[str, list[bool]] = {}
        query_items: dict[str, dict] = {}
        for future in as_completed(future_to_info):
            item, _ = future_to_info[future]
            query = item["query"]
            query_items[query] = item
            if query not in query_triggers:
                query_triggers[query] = []
            try:
                query_triggers[query].append(future.result())
            except Exception as e:
                print(f"Warning: query failed: {e}", file=sys.stderr)
                query_triggers[query].append(False)

    for query, triggers in query_triggers.items():
        item = query_items[query]
        trigger_rate = sum(triggers) / len(triggers)
        should_trigger = item["should_trigger"]
        if should_trigger:
            did_pass = trigger_rate >= trigger_threshold
        else:
            did_pass = trigger_rate < trigger_threshold
        results.append({
            "query": query,
            "should_trigger": should_trigger,
            "trigger_rate": trigger_rate,
            "triggers": sum(triggers),
            "runs": len(triggers),
            "pass": did_pass,
        })

    passed = sum(1 for r in results if r["pass"])
    total = len(results)

    return {
        "skill_name": skill_name,
        "description": description,
        "results": results,
        "summary": {
            "total": total,
            "passed": passed,
            "failed": total - passed,
        },
    }


def main():
    parser = argparse.ArgumentParser(
        description="Run trigger evaluation for a skill description"
    )
    parser.add_argument("--eval-set", required=True, help="Path to eval set JSON file")
    parser.add_argument("--skill-path", required=True, help="Path to skill directory")
    parser.add_argument("--description", default=None, help="Override description to test")
    parser.add_argument("--num-workers", type=int, default=10, help="Number of parallel workers")
    parser.add_argument("--timeout", type=int, default=30, help="Timeout per query in seconds")
    parser.add_argument("--runs-per-query", type=int, default=3, help="Number of runs per query")
    parser.add_argument("--trigger-threshold", type=float, default=0.5, help="Trigger rate threshold")
    parser.add_argument("--model", default=None, help="Model to use (default: user's configured model)")
    parser.add_argument(
        "--model-cmd",
        default="claude -p",
        help="CLI command to invoke the model (e.g., 'claude -p', 'opencode -p')",
    )
    parser.add_argument(
        "--skill-inject-dir",
        default=None,
        help="Directory to place skill file for agent discovery (default: auto-detect from agent markers)",
    )
    parser.add_argument(
        "--project-marker",
        default=".opencode",
        help="Directory marker to find project root (default: .opencode)",
    )
    parser.add_argument("--verbose", action="store_true", help="Print progress to stderr")
    args = parser.parse_args()

    eval_set = json.loads(Path(args.eval_set).read_text())
    skill_path = Path(args.skill_path)

    if not (skill_path / "SKILL.md").exists():
        print(f"Error: No SKILL.md found at {skill_path}", file=sys.stderr)
        sys.exit(1)

    name, original_description, content = parse_skill_md(skill_path)
    description = args.description or original_description
    project_root = find_project_root(args.project_marker)

    if args.verbose:
        print(f"Project root: {project_root}", file=sys.stderr)
        print(f"Model cmd: {args.model_cmd}", file=sys.stderr)
        print(f"Evaluating description: {description[:80]}...", file=sys.stderr)

    output = run_eval(
        eval_set=eval_set,
        skill_name=name,
        description=description,
        num_workers=args.num_workers,
        timeout=args.timeout,
        project_root=project_root,
        runs_per_query=args.runs_per_query,
        trigger_threshold=args.trigger_threshold,
        model_cmd=args.model_cmd,
        skill_inject_dir=args.skill_inject_dir,
        model=args.model,
    )

    if args.verbose:
        summary = output["summary"]
        print(f"Results: {summary['passed']}/{summary['total']} passed", file=sys.stderr)
        for r in output["results"]:
            status = "PASS" if r["pass"] else "FAIL"
            rate_str = f"{r['triggers']}/{r['runs']}"
            print(f"  [{status}] rate={rate_str} expected={r['should_trigger']}: {r['query'][:70]}", file=sys.stderr)

    print(json.dumps(output, indent=2))


if __name__ == "__main__":
    main()
