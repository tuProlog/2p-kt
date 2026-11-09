# Clause storage and indexing

A Prolog engine spends most of its time doing one thing over and over: given a goal, find the clauses of the
knowledge base whose head unifies with it, in order. Get this operation right and resolution is fast; get it wrong
and every call degenerates into a linear scan of the whole program. This page explains *how* the `theory` module
stores clauses and retrieves them: the data structures behind `Theory`, `MutableTheory` and the clause collections,
the indexing scheme, the logical update view, and the literature they come from. For *what* the API offers, see the
[Theory and clause collections API](../reference/theory-api.md); for task-oriented recipes, see
[Use theories and clause collections](../how-to/use-theories-and-clause-collections.md).

## Two stores, one contract

Every clause collection is a thin façade over one of two internal *stores*:

| | `FrozenStore` | `MutableStore` |
|---|---|---|
| Used by | `Theory`, and every immutable collection | `MutableTheory`, every mutable collection, `ClauseMap` |
| Serves | static knowledge bases: loaded once, queried many times | dynamic knowledge bases: `assert/1`, `retract/1` on many resolution steps |
| Global order | an array of clauses | a deque of entries, with tombstones |
| Updates | return a new store, sharing untouched procedures | in place, amortised O(1) after lookup |
| Concurrent reads and writes | n/a (immutable) | logical update view, through generations |

![Clause stores](../assets/diagrams/clause-store.svg)

Both group clauses into **procedures**: the clauses sharing the functor and arity of their head (directives form one
more procedure). A goal only ever looks at its own procedure, so `parent(X, Y)` never touches `sibling/2`, and clause
order only needs preserving *within* a procedure; iterating the whole collection follows a separate, global order.

### The frozen store: arrays and structural sharing

A `FrozenStore` keeps the clauses in an array, in order, and each procedure in its own array. Updates never mutate
anything: `assertZ` copies the global array and the one procedure it touches, and shares every other procedure,
together with the indexes already built on it, with the original store. This is *persistent* behaviour at procedure
granularity: updating a 10,000-clause theory with one clause costs O(n) for the global array and O(k) for its
procedure of k clauses, but no index has to be rebuilt elsewhere. A full persistent data structure (e.g. a hash array
mapped trie) would make single updates cheaper still, but solvers never update their static knowledge base clause by
clause, only in batches, so it would not pay for its extra cost on lookups.

### The mutable store: entries, tombstones and compaction

A `MutableStore` wraps each clause in an `Entry` recording its position in the global order and its lifetime (see
[the logical update view](#the-logical-update-view) below), and keeps entries in array-backed deques that grow at
both ends: one global deque, one per procedure, one per index bucket. Asserting appends (or prepends) the new entry to
its procedure's deque and to the matching bucket of every index already built, in amortised constant time.

Retracting only *marks* an entry as erased (a tombstone): removing it from the middle of a deque would cost O(n), and
lookups already running may still have to yield it. Erased entries are physically dropped by *compaction*, which runs
once they outnumber the live ones (with a small slack, to avoid compacting tiny deques over and over), so storage
stays proportional to the live clauses and the amortised cost of a retraction stays constant. Each index bucket
compacts on its own, so indexes survive compactions instead of being rebuilt every n removals.

## Indexing

### Keys, not unification

An index maps *keys* to clauses. A key is a cheap abstraction of a term, an `IndexKey`, computed by the collection's
`Unificator` through `Unificator.indexKey(term)`: an atom by its value, a number by its value, a compound term by
functor and arity, a variable as a `Wildcard`. The contract mirrors the one between `hashCode` and `equals`:

> if two terms unify, either their keys are equal, or one of them is not exact (a wildcard).

Keys may therefore merge terms that do not unify (indexing becomes less selective, never wrong), but never separate
terms that do. Since the unificator owns the keys, indexing honours its notion of equality: `Unificator.naive()`,
which unifies `1` with `1.0`, keys numbers by value, so an indexed lookup for `n(1.0)` finds `n(1)`. A custom
unificator accepting pairs with different principal symbols must override `indexKey` accordingly, see
[Implement a custom Unificator](../how-to/implement-a-custom-unificator.md#5-keep-indexing-consistent).

### Demand-driven, multi-argument indexes

Classical Prolog systems index the *first* argument only, through the WAM's `switch_on_term` instruction
[[Warren 1983](#references); [Aït-Kaci 1991](#references)]: `p(a, X)` is fast, `p(X, a)` scans the whole procedure.
The `theory` module follows instead the *demand-driven* (or *just-in-time*) scheme of YAP [[Santos Costa et al.
2007](#references)] and SWI-Prolog [[Wielemaker et al. 2012](#references)]:

1. Procedures with fewer than 4 clauses are simply scanned: an index would cost more than it saves.
2. Otherwise, the first lookup binding argument *i* (to a term with an exact key) builds an index on argument *i*;
   an argument where no clause has an exact key is remembered as not indexable, so as not to try again.
3. Every lookup then considers each argument it binds, and uses the index yielding the **fewest candidates**:
   `p(X, a, Y)` is as fast as `p(a, X, Y)` whenever the second argument discriminates.

Indexes are built lazily, so a theory pays only for the access patterns its program actually uses. In a frozen
store, an index is built once and published by replacing an immutable map of indexes; threads racing to build it at
worst build it twice. In a mutable store, a built index is maintained on every insertion.

### Deep indexing

An argument's index cannot tell apart clauses sharing its key there: in a procedure of facts `p(f(k1, …))`,
`p(f(k2, …))`, …, all clauses have key `f/2` at argument 1. Where the query's bucket at a position holds more than one
clause, lookups also consider the arguments of the compound term found there, with indexes of their own, down to
depth 4 (`MAX_INDEX_DEPTH`): this is SWI-Prolog's *deep indexing*. Indexed positions are therefore *paths* (argument
1, then its argument 2, …), encoded in an `Int` with 8 bits per step.

### From candidates to matches

An index bucket lists, in clause order, the clauses having a given key at its position, and the clauses having a
variable there, which are candidates for every key. A lookup merges the two sorted lists lazily, so asking for the
first match stops at the first match. Before unifying a candidate, a **key pre-check** compares the keys of the
query's other bound arguments with the candidate's, rejecting most non-matching clauses without unifying
anything; the check is skipped for the first candidate an index selects, as a selective index usually yields just one.

Two lookups are offered on top of this:

- `get(query)` unifies the surviving candidates with the query (according to the unificator) and yields those that
  unify: what Prolog means by "the clauses for this goal";
- `candidates(query)` stops before unifying, for callers that unify each clause themselves anyway. Solvers use it:
  their resolution step renames each candidate and unifies it with the goal once, and that unifier is the one
  resolution continues with, so no clause is unified twice. As only clauses unifying with the goal are kept as
  alternatives, the solver still knows exactly when a goal has no other alternative, and opens no choice point.

### Variants

Sets and maps identify clauses up to variable renaming (`Term.isVariantOf`), tags ignored. Rather than maintaining a
separate hash structure, variant checks go through the same indexes: a variant of `c` necessarily unifies with `c`,
so it is searched among the clauses matching `c`.

## The logical update view

ISO Prolog prescribes the *logical update view* for dynamic predicates [[ISO/IEC 13211-1, 7.5.4](#references)]: a
call sees the clauses as they were when it started, whatever `assert/1` and `retract/1` do while it backtracks through
them. A naive implementation copies the matching clauses on every call; the `theory` module implements it with
**generations** [[Lindholm and O'Keefe 1987](#references)]:

- every update increments the store's generation;
- every entry records the generation it was added in (`born`) and the one it was removed in (`erased`, infinity while
  alive);
- a lookup or iteration captures the current generation when it starts, and only yields the entries alive then:
  `born <= generation < erased`.

Lookups capture `(array, from, to)` views of the deques they read. Deques only ever write outside the range of
existing views (they grow at both ends, and compaction or growth allocates a new array), so a lookup stays valid while
the store changes, without copying anything. This is why `MutableTheory` lookups are both lazy and safe under
mutation, and why the solvers no longer copy the clauses of a dynamic predicate on every call.

## Why this design

Resolution needs one operation to be fast (the clauses for this goal, in order) and two kinds of knowledge base with
opposite update patterns. A single storage scheme, with read- and write-optimised stores behind the same `Theory`
contract, gives both without making the solver aware of the difference. The same stores serve every clause
collection, so programmatic manipulation of logic programs gets the same indexing as resolution. The alternatives
weighed against benchmarks (`examples/benchmarks/README.md`) and not adopted:

- *joint (multi-argument) indexes and tries* [[Ramakrishnan et al. 1999](#references)]: with the key pre-check, a
  lookup binding two moderately selective arguments costs a few key comparisons per candidate and no extra
  unification, which no solver workload so far needs to improve on;
- *a full persistent store* for `Theory`: single-clause updates of static knowledge bases are not hot (see above);
- *a Rete network*, used by earlier versions of 2P-Kt: Rete matches many patterns against a changing set of facts,
  the opposite of what resolution asks (many facts against one goal), and it indexed first arguments only.

## References

- **Aït-Kaci 1991.** H. Aït-Kaci. *Warren's Abstract Machine: A Tutorial Reconstruction*. MIT Press, 1991.
- **ISO/IEC 13211-1.** *Information technology — Programming languages — Prolog — Part 1: General core*. ISO, 1995;
  section 7.5.4, "A logical database update".
- **Lindholm and O'Keefe 1987.** T. Lindholm, R. A. O'Keefe. *Efficient Implementation of a Defensible Semantics for
  Dynamic Prolog Code*. In: Proceedings of the 4th International Conference on Logic Programming (ICLP), MIT Press,
  1987, pp. 21–39.
- **Ramakrishnan et al. 1999.** I. V. Ramakrishnan, P. Rao, K. Sagonas, T. Swift, D. S. Warren. *Efficient Access
  Mechanisms for Tabled Logic Programs*. Journal of Logic Programming 38(1), 1999, pp. 31–54.
- **Santos Costa et al. 2007.** V. Santos Costa, K. Sagonas, R. Lopes. *Demand-Driven Indexing of Prolog Clauses*. In:
  Logic Programming, 23rd International Conference (ICLP 2007), LNCS 4670, Springer, 2007, pp. 395–409.
- **Warren 1983.** D. H. D. Warren. *An Abstract Prolog Instruction Set*. Technical Note 309, SRI International, 1983.
- **Wielemaker et al. 2012.** J. Wielemaker, T. Schrijvers, M. Triska, T. Lager. *SWI-Prolog*. Theory and Practice of
  Logic Programming 12(1-2), 2012, pp. 67–96; and the SWI-Prolog reference manual, section "Just-in-time clause
  indexing" (including "Deep indexing").
