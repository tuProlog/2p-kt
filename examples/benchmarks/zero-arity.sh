#!/usr/bin/env bash
# Zero-arity lookup check (#1019): runs the four size=1000 zero-arity cases of TheoryLookupBenchmark on two refs, with
# the settings of the longer check in README.md, alternating refs per round, then prints the comparison.
# Usage: examples/benchmarks/zero-arity.sh [before-ref] [after-ref] [rounds]
#   defaults: origin/ccr-0b4a13b3-ejoi9f (PR head), origin/ccr-0b4a13b3-ejoi9f-zero-arity (fix), 1 round
set -euo pipefail
before=${1:-origin/ccr-0b4a13b3-ejoi9f}
after=${2:-origin/ccr-0b4a13b3-ejoi9f-zero-arity}
rounds=${3:-1}
root=$(git rev-parse --show-toplevel)
work=$(mktemp -d)
out="$root/examples/benchmarks/results"
args='-wi 3 -i 5 -w 1s -r 1s -f 2 -p size=1000 -p query=zero-arity benchmarks.theory.TheoryLookupBenchmark[.]'
trap 'for d in "$work"/*/; do git -C "$root" worktree remove --force "$d" 2>/dev/null || true; done; rm -rf "$work"' EXIT

git -C "$root" fetch -q origin
# worktrees and result files are named by role, not by ref: one ref name can be a prefix of the other
git -C "$root" worktree add -q --detach "$work/before" "$before"
git -C "$root" worktree add -q --detach "$work/after" "$after"

for round in $(seq 1 "$rounds"); do
  # alternate which ref goes first, so drift (thermal, background load) does not favour one side
  if (( round % 2 )); then order=(before after); else order=(after before); fi
  for role in "${order[@]}"; do
    dir="$work/$role"
    sha=$(git -C "$dir" rev-parse --short=9 HEAD)
    (cd "$dir" && ./gradlew -q :examples:timingBenchmarks -PjmhArgs="$args")
    cp "$dir/examples/build/reports/benchmarks/jmh.json" "$out/$(date +%F)-zero-arity-$role-$sha-r$round.json"
  done
done

for round in $(seq 1 "$rounds"); do
  echo "## Round $round"
  python3 "$root/examples/benchmarks/compare.py" \
    "$(ls -t "$out"/*-zero-arity-before-*-r"$round".json | head -1)" \
    "$(ls -t "$out"/*-zero-arity-after-*-r"$round".json | head -1)"
done
