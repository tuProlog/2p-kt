"""Collects every executed test case from Gradle's JUnit XML reports into one CSV (Kotest migration, #1017).

Usage: python3 migration/inventory.py [repo-root] [newer-than-file] > migration/<name>.csv
Run after `./gradlew jvmTest jsTest --continue`; a per-module/platform summary goes to stderr.
Pass a file touched right before the Gradle run as `newer-than-file` to skip stale reports left behind by
earlier runs or by modules that no longer exist.
"""

import csv
import sys
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path

root = Path(sys.argv[1] if len(sys.argv) > 1 else ".").resolve()
since = Path(sys.argv[2]).stat().st_mtime if len(sys.argv) > 2 else 0
rows = []
for report in root.glob("**/build/test-results/*/TEST-*.xml"):
    if "node_modules" in report.parts or report.stat().st_mtime < since:
        continue
    module = str(report.relative_to(root)).split("/build/")[0]
    task = report.parent.name  # e.g. jvmTest, jsNodeTest, jsBrowserTest, test, swingE2eTest
    for case in ET.parse(report).getroot().iter("testcase"):
        if case.find("skipped") is not None:
            status = "skipped"
        elif case.find("failure") is not None or case.find("error") is not None:
            status = "failed"
        else:
            status = "passed"
        rows.append((module, task, case.get("classname", ""), case.get("name", ""), status, case.get("time", "")))

rows.sort()
out = csv.writer(sys.stdout, lineterminator="\n")
out.writerow(("module", "task", "class", "test", "status", "time"))
out.writerows(rows)

summary = Counter((m, t, s) for m, t, _, _, s, _ in rows)
for (m, t, s), n in sorted(summary.items()):
    print(f"{m:28} {t:16} {s:8} {n}", file=sys.stderr)
print(f"total {len(rows)}", file=sys.stderr)
