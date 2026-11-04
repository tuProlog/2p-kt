#!/usr/bin/env python3
"""Tabulates a JMH JSON result file (#1020) side by side per `engine` parameter: score, allocations, speed-up."""
import json
import sys


def allocated(entry):
    metric = entry.get("secondaryMetrics", {}).get("gc.alloc.rate.norm") or entry.get("secondaryMetrics", {}).get("·gc.alloc.rate.norm")
    return metric["score"] if metric else None


def main(path, baseline="equational", candidate="worklist"):
    rows = {}
    for entry in json.load(open(path)):
        params = dict(entry.get("params", {}))
        engine = params.pop("engine")
        key = (entry["benchmark"].rsplit(".", 2)[-2], tuple(sorted(params.items())))
        rows.setdefault(key, {})[engine] = entry
    unit = None
    print(f"| Benchmark | Parameters | {baseline} | {candidate} | Speed-up | {baseline} B/op | {candidate} B/op |")
    print("|---|---|---:|---:|---:|---:|---:|")
    for (benchmark, params), engines in rows.items():
        old, new = engines.get(baseline), engines.get(candidate)
        if old is None or new is None:
            continue
        unit = new["primaryMetric"]["scoreUnit"]
        old_score, new_score = old["primaryMetric"]["score"], new["primaryMetric"]["score"]
        # throughput: higher is better; average time: lower is better
        speedup = new_score / old_score if unit.endswith("/s") else old_score / new_score
        described = ", ".join(f"{k}={v}" for k, v in params)
        old_alloc, new_alloc = allocated(old), allocated(new)
        print(
            f"| {benchmark} | {described} | {old_score:.3f} | {new_score:.3f} | {speedup:.2f}x | "
            f"{old_alloc:,.0f} | {new_alloc:,.0f} |"
        )
    print(f"\nScores in {unit}.")


if __name__ == "__main__":
    main(*sys.argv[1:])
