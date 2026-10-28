"""Condenses test results, coverage and Gradle scans into the CI run summary.

Each CI job runs `collect` (from the repository root, after Gradle) to boil its JUnit XML reports, Kover report and
Gradle scan links down to a small JSON digest, uploaded as an artifact. The `report` job then reads every digest
(plus, optionally, the build digest of the base branch's last successful run, for the coverage trend) and writes
one Markdown summary, emitting GitHub annotations for failures and coverage drops.

    python3 ci-summary.py collect --out digest.json --kind build|matrix [--dim os=ubuntu ...] [--outcome success]
    python3 ci-summary.py report --digests DIR [--baseline FILE] --summary "$GITHUB_STEP_SUMMARY"
"""

import argparse
import json
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

E2E_TASKS = {"swingE2eTest", "browserE2ETest"}
MAX_FAILURES = 100  # per digest, so that a catastrophic run keeps its artifact (and the summary) small
MAX_ANNOTATIONS = 10  # GitHub shows at most 10 error annotations per step anyway
COVERAGE_DROP_ALERT = 0.1  # percentage points
KOVER_REPORT = Path("build/reports/kover/report.xml")
SCANS_FILE = Path("build/gradle-scans.txt")


# ---------------------------------------------------------------------------------------------------- collect


def collect_tests():
    suites = defaultdict(lambda: {"tests": 0, "failures": 0, "skipped": 0})
    failed = []
    for report in sorted(Path(".").glob("**/build/test-results/*/**/*.xml")):
        if "node_modules" in report.parts:
            continue
        parts = report.as_posix().split("/build/test-results/")
        module, task = parts[0], parts[1].split("/")[0]
        if not Path(module, "build.gradle.kts").exists():
            continue  # stale build directory of a removed module
        try:
            root = ET.parse(report).getroot()
        except ET.ParseError:
            continue
        for case in root.iter("testcase"):
            suite = suites[(module, task)]
            suite["tests"] += 1
            problem = case.find("failure")
            if problem is None:
                problem = case.find("error")
            if problem is not None:
                suite["failures"] += 1
                if len(failed) < MAX_FAILURES:
                    message = (problem.get("message") or problem.text or "").strip().splitlines()
                    failed.append(
                        {
                            "module": module,
                            "task": task,
                            "class": case.get("classname", ""),
                            "name": case.get("name", ""),
                            "message": message[0][:300] if message else "",
                        }
                    )
            elif case.find("skipped") is not None:
                suite["skipped"] += 1
    return [{"module": m, "task": t, **counts} for (m, t), counts in sorted(suites.items())], failed


def collect_coverage():
    if not KOVER_REPORT.exists():
        return None
    files = subprocess.run(["git", "ls-files", "*/src/*/kotlin/*.kt"], capture_output=True, text=True).stdout
    module_of = {}
    for path in files.split():
        match = re.match(r"(.*?)/src/(\w+)/kotlin/(.*)", path)
        if match and "Test" not in match.group(2):
            module_of.setdefault(match.group(3), match.group(1))
    modules = defaultdict(lambda: [0, 0])
    total = [0, 0]
    for package in ET.parse(KOVER_REPORT).getroot().iter("package"):
        for source in package.iter("sourcefile"):
            counter = source.find("counter[@type='LINE']")
            if counter is None:
                continue
            covered, missed = int(counter.get("covered")), int(counter.get("missed"))
            module = module_of.get(f"{package.get('name')}/{source.get('name')}", "?")
            modules[module][0] += covered
            modules[module][1] += missed
            total[0] += covered
            total[1] += missed
    return {"total": total, "modules": dict(modules)}


def collect(args):
    suites, failed = collect_tests()
    scans = SCANS_FILE.read_text().split() if SCANS_FILE.exists() else []
    digest = {
        "kind": args.kind,
        "dims": dict(d.split("=", 1) for d in args.dim),
        "outcome": args.outcome,
        "suites": suites,
        "failed": failed,
        "coverage": collect_coverage() if args.kind == "build" else None,
        "scans": scans,
    }
    Path(args.out).write_text(json.dumps(digest))


# ----------------------------------------------------------------------------------------------------- report


def pct(covered, missed):
    total = covered + missed
    return 100.0 * covered / total if total else 0.0


def totals(suites):
    return {k: sum(s[k] for s in suites) for k in ("tests", "failures", "skipped")}


def status_icon(digest):
    if digest["outcome"] == "skipped":
        return "⏭️"  # inputs unchanged since the last green run: tests were not re-run
    failures = totals(digest["suites"])["failures"]
    if failures:
        return f"❌ {failures}"
    if digest["outcome"] not in ("success", ""):
        return "⚠️"  # the job failed without any failing test: compilation, setup, infrastructure
    return "✅"


def is_bad(digest):
    return status_icon(digest).startswith(("❌", "⚠️"))


def failure_rows(failed):
    rows = ["| Module | Task | Test | Message |", "|---|---|---|---|"]
    for f in failed:
        test = f"{f['class'].rsplit('.', 1)[-1]} › {f['name']}".replace("|", "\\|")
        message = f["message"].replace("|", "\\|")
        rows.append(f"| `{f['module']}` | `{f['task']}` | {test} | {message} |")
    return rows


def section_tests(title, suites, failed, truncated):
    if not suites:
        return [f"## {title}", "", "No results (the job didn't get as far as running these tests).", ""]
    t = totals(suites)
    icon = "❌" if t["failures"] else "✅"
    out = [f"## {title}", "", f"{icon} **{t['tests']}** tests, **{t['failures']}** failed, {t['skipped']} skipped", ""]
    if failed:
        out += failure_rows(failed)
        if truncated:
            out.append(f"| … | | only the first {MAX_FAILURES} failures are listed | |")
        out.append("")
    out += ["<details><summary>Per module</summary>", "", "| Module | Task | Tests | Failed | Skipped |", "|---|---|--:|--:|--:|"]
    for s in suites:
        out.append(f"| `{s['module']}` | `{s['task']}` | {s['tests']} | {s['failures']} | {s['skipped']} |")
    return out + ["", "</details>", ""]


def matrix_column(dims):
    if dims.get("runtime") == "js":
        return f"Node {dims.get('version')}"
    return f"JDK {dims.get('version')} {dims.get('dist', '')}".strip()


def section_matrix(matrix):
    out = ["## Test extensively", ""]
    if not matrix:
        return out + ["Not run (it only starts once `lint` and `build` succeed).", ""]
    oses = sorted({d["dims"]["os"] for d in matrix})
    columns = sorted({matrix_column(d["dims"]) for d in matrix}, key=lambda c: (c.startswith("Node"), c))
    cell = {(d["dims"]["os"], matrix_column(d["dims"])): d for d in matrix}
    out += ["| OS | " + " | ".join(columns) + " |", "|---|" + "---|" * len(columns)]
    for os_name in oses:
        row = [status_icon(cell[(os_name, c)]) if (os_name, c) in cell else " " for c in columns]
        out.append(f"| **{os_name}** | " + " | ".join(row) + " |")
    out += ["", "✅ passed · ❌ n failing tests · ⚠️ job failed outside tests · ⏭️ skipped, inputs unchanged since a green run", ""]

    # Projections: for each dimension value, how many of its jobs went wrong. A value whose jobs ALL fail, while
    # others pass, points at that OS / runtime / version.
    projections = {
        "OS": lambda d: d["dims"]["os"],
        "Runtime": lambda d: "JVM" if d["dims"]["runtime"] == "jvm" else "JS (Node)",
        "Version": lambda d: ("JDK " if d["dims"]["runtime"] == "jvm" else "Node ") + d["dims"]["version"],
        "JDK distribution": lambda d: d["dims"].get("dist"),
    }
    bad_jobs = [d for d in matrix if is_bad(d)]
    if not bad_jobs:
        return out + [f"All {len(matrix)} jobs passed (or were skipped as unchanged since a green run).", ""]
    out += ["| Dimension | Value | Jobs with problems |", "|---|---|---|"]
    suspects = []
    for dimension, key in projections.items():
        groups = defaultdict(list)
        for d in matrix:
            if key(d):
                groups[key(d)].append(d)
        for value, jobs in sorted(groups.items()):
            bad = sum(is_bad(j) for j in jobs)
            out.append(f"| {dimension} | {value} | {'🔴' if bad == len(jobs) else '🟠' if bad else '🟢'} {bad}/{len(jobs)} |")
            if bad and bad_jobs and all(key(j) == value for j in bad_jobs) and len(groups) > 1:
                suspects.append(f"{dimension.lower()} **{value}**")
    out.append("")
    if suspects:
        out += ["> [!WARNING]", "> Every job with problems shares " + " and ".join(suspects) + ".", ""]

    by_test = defaultdict(list)
    for d in matrix:
        for f in d["failed"]:
            by_test[(f["module"], f["class"].rsplit(".", 1)[-1], f["name"])].append(
                f"{d['dims']['os']} / {matrix_column(d['dims'])}"
            )
    if by_test:
        out += ["| Module | Test | Failed on |", "|---|---|---|"]
        for (module, cls, name), where in sorted(by_test.items()):
            out.append(f"| `{module}` | {cls} › {name} | {', '.join(sorted(where))} |")
        out.append("")
    return out


def section_coverage(build, baseline):
    out = ["## Coverage (JVM)", ""]
    coverage = build.get("coverage") if build else None
    if not coverage:
        return out + ["No Kover report (it is only produced when every test passes).", ""], []
    base = (baseline or {}).get("coverage")
    total = pct(*coverage["total"])
    line = f"**{total:.1f}%** of lines"
    alerts = []
    if base:
        delta = total - pct(*base["total"])
        line += f" ({delta:+.2f} pp vs. base)"
        if delta < -COVERAGE_DROP_ALERT:
            alerts.append(f"Line coverage dropped by {-delta:.2f} percentage points ({pct(*base['total']):.1f}% → {total:.1f}%).")
    else:
        line += " (no baseline: the base branch has no successful run with a coverage digest yet)"
    out += [line, ""]
    if alerts:
        out += ["> [!CAUTION]", "> " + alerts[0], ""]
    out += ["<details><summary>Per module</summary>", "", "| Module | Lines | Coverage | Δ |", "|---|--:|--:|--:|"]
    for module, (covered, missed) in sorted(coverage["modules"].items()):
        delta = ""
        if base and module in base["modules"]:
            d = pct(covered, missed) - pct(*base["modules"][module])
            delta = f"{'🔻' if d < -COVERAGE_DROP_ALERT else ''}{d:+.2f}"
        out.append(f"| `{module}` | {covered + missed} | {pct(covered, missed):.1f}% | {delta} |")
    return out + ["", "</details>", ""], alerts


def report(args):
    digests = [json.loads(p.read_text()) for p in sorted(Path(args.digests).glob("**/*.json"))]
    build = next((d for d in digests if d["kind"] == "build"), None)
    matrix = [d for d in digests if d["kind"] == "matrix"]
    baseline = json.loads(Path(args.baseline).read_text()) if args.baseline and Path(args.baseline).exists() else None

    out = ["# CI summary", ""]
    scans = [s for d in digests for s in d["scans"]]
    if scans:
        out += ["**Gradle scans** (published for failed builds only): " + " · ".join(f"[{i + 1}]({s})" for i, s in enumerate(scans)), ""]
    else:
        out += ["No Gradle scans: they are published only when a Gradle build fails.", ""]

    suites = build["suites"] if build else []
    failed = build["failed"] if build else []
    truncated = build is not None and totals(suites)["failures"] > len(failed)
    unit_suites = [s for s in suites if s["task"] not in E2E_TASKS]
    e2e_suites = [s for s in suites if s["task"] in E2E_TASKS]
    out += section_matrix(matrix)
    out += section_tests("Unit tests", unit_suites, [f for f in failed if f["task"] not in E2E_TASKS], truncated)
    out += section_tests("End-to-end tests", e2e_suites, [f for f in failed if f["task"] in E2E_TASKS], False)
    coverage_section, alerts = section_coverage(build, baseline)
    out += coverage_section
    Path(args.summary).open("a").write("\n".join(out) + "\n")

    annotations = 0
    for d in [build, *matrix]:
        for f in (d or {}).get("failed", []):
            if annotations == MAX_ANNOTATIONS:
                break
            where = f" ({d['dims']['os']} / {matrix_column(d['dims'])})" if d["kind"] == "matrix" else ""
            print(f"::error title=Test failed{where}: {f['module']} › {f['name']}::{f['message']}")
            annotations += 1
    for alert in alerts:
        print(f"::warning title=Coverage dropped::{alert}")


def main():
    parser = argparse.ArgumentParser()
    commands = parser.add_subparsers(dest="command", required=True)
    c = commands.add_parser("collect")
    c.add_argument("--out", required=True)
    c.add_argument("--kind", choices=("build", "matrix"), required=True)
    c.add_argument("--dim", action="append", default=[])
    c.add_argument("--outcome", default="")
    r = commands.add_parser("report")
    r.add_argument("--digests", required=True)
    r.add_argument("--baseline")
    r.add_argument("--summary", required=True)
    args = parser.parse_args()
    collect(args) if args.command == "collect" else report(args)


if __name__ == "__main__":
    sys.exit(main())
