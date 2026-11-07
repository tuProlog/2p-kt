#!/usr/bin/env python3
"""Compares two JMH JSON result files (#1012): prints, per benchmark and parameters, both scores and the change."""
import json
import lzma
import sys


def key(entry):
    params = tuple(sorted((k, v) for k, v in entry.get("params", {}).items() if k != "chronometer"))
    return entry["benchmark"].rsplit(".", 2)[-2:][0] + "." + entry["benchmark"].rsplit(".", 1)[-1], params


def load(path):
    with (lzma.open if path.endswith(".xz") else open)(path, "rt") as source:
        return json.load(source)


def main(before_path, after_path):
    before = {key(e): e for e in load(before_path)}
    after_entries = load(after_path)
    print("| Benchmark | Parameters | Before | After | Change | Unit |")
    print("|---|---|---:|---:|---:|---|")
    for entry in after_entries:
        k = key(entry)
        params = ", ".join(f"{p}={v}" for p, v in sorted(entry.get("params", {}).items()))
        metric = entry["primaryMetric"]
        old = before.get(k)
        if old is None:
            print(f"| {k[0]} | {params} | - | {metric['score']:.3f} | - | {metric['scoreUnit']} |")
            continue
        old_score = old["primaryMetric"]["score"]
        change = (metric["score"] - old_score) / old_score * 100
        print(f"| {k[0]} | {params} | {old_score:.3f} | {metric['score']:.3f} | {change:+.1f}% | {metric['scoreUnit']} |")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
