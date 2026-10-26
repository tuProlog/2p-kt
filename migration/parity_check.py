"""Checks a migration batch id by id against the baseline (#1017).

Usage: parity_check.py NEW_INVENTORY.csv MAPPINGS.jsonl MODULE...
Every baseline case of a mapped old class must reappear under its new spec and leaf with the same status, per task;
baseline cases of the modules that vanished without a mapping, and new cases nobody maps to, are listed.
"""
import csv, json, re, sys
from collections import Counter

inv, maps, modules = sys.argv[1], sys.argv[2], set(sys.argv[3:])
norm_c = lambda c: re.sub(r"^js\w*Test\.", "", c)
norm_t = lambda t: re.sub(r"(\(\)|\[js, [^]]*\]|\[jvm\])$", "", t)
m = {(r["old_class"], r["old_test"]): (r["new_spec"], r["new_case"]) for r in map(json.loads, open(maps)) if r["module"] in modules}
base = [r for r in csv.DictReader(open("migration/baseline.csv")) if r["module"] in modules]
new = {(r["module"], r["task"], norm_c(r["class"]), norm_t(r["test"])): r["status"] for r in csv.DictReader(open(inv)) if r["module"] in modules}
seen, problems, ok = set(), Counter(), 0
for r in base:
    k = (norm_c(r["class"]), norm_t(r["test"]))
    if k in m:
        nk = (r["module"], r["task"], *m[k])
    else:
        nk = (r["module"], r["task"], *k)
    seen.add(nk)
    if nk not in new:
        problems["missing"] += 1
        print("missing", r["module"], r["task"], *k, "->", nk[2:])
    elif new[nk] != r["status"]:
        problems["status"] += 1
        print("status", nk, r["status"], "->", new[nk])
    else:
        ok += 1
extra = [k for k in new if k not in seen and not k[3].startswith("Kotest")]
for k in extra:
    print("new-only", k, new[k])
print(f"ok {ok}; {dict(problems)}; new-only {len(extra)}", file=sys.stderr)
