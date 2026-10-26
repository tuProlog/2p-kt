"""Appends plain2spec.py JSON-line mappings to migration/parity.csv (run from the repo root)."""
import csv, json, sys
rows = [json.loads(l) for l in open(sys.argv[1]) if l.startswith("{")]
with open("migration/parity.csv", "a") as out:
    csv.writer(out, lineterminator="\n").writerows([r["module"], r["old_class"], r["old_test"], r["new_spec"], r["new_case"], 1] for r in rows)
print(len(rows), "rows")
