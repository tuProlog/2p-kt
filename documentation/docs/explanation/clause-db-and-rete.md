# Clause Databases and Indexing

A Prolog engine spends most of its time doing one thing over and over: given a goal, find the clauses in the
knowledge base whose head *might* unify with it. Get this operation right and resolution is fast; get it wrong
and every call degenerates into a linear scan of the whole program. This page explains how 2P-Kt models
knowledge bases (`Theory`), how it stores clauses (`ClauseCollection`), and what indexing strategy it actually
uses to make clause retrieval fast.

## Theories are just clause collections with a Prolog-shaped face

In 2P-Kt, a knowledge base is a `Theory`:

```kotlin
--8<-- "theory/src/commonMain/kotlin/it/unibo/tuprolog/theory/Theory.kt:17:23"
```

`Theory` is deliberately a thin, Prolog-flavoured façade (`assertA`/`assertZ`/`retract`/`abolish`, mirroring
ISO built-ins) over a lower-level, general-purpose storage abstraction: `ClauseCollection`, in the
`it.unibo.tuprolog.collections` package. The split exists because clause *storage* (how clauses are kept,
searched, and mutated) is orthogonal to the *vocabulary* Prolog programmers expect. `Theory` speaks the
vocabulary; `ClauseCollection` does the storing.

```kotlin
--8<-- "theory/src/commonMain/kotlin/it/unibo/tuprolog/collections/ClauseCollection.kt:11:34"
```

Two design axes recur throughout the `:theory` module, and every concrete type sits at a point in this
2×2 space:

- **mutable vs. immutable** — a `MutableClauseCollection`/`MutableTheory` edits itself in place; an immutable
  one returns a fresh collection from every `add`/`retrieve` call, leaving the original untouched. Immutability
  is the default and matches the immutable-everything philosophy of `:core` (terms, substitutions, clauses are
  all persistent data structures); mutability is opt-in, via `toMutableTheory()`, for the (common) case where a
  Prolog program repeatedly asserts/retracts clauses and copying on every step would be wasteful.
- **indexed vs. listed** — `IndexedTheory` trades memory and update cost for fast lookup; `ListedTheory` is a
  thin wrapper around an ordered list, cheap to build and to keep in insertion order, but linear to query. Which
  one a solver uses is a knob, not a hardcoded choice: `Theory.empty()` gives you the indexed variant by default,
  `Theory.emptyListed()` gives you the listed one.

Why keep both? A knowledge base loaded once and queried thousands of times (a typical `staticKb`) wants
indexing. A knowledge base that is asserted/retracted on nearly every resolution step, or where clause order
must be preserved exactly and cheaply (e.g. `clause/2` introspection, or very small dynamic databases), may not
be worth indexing at all. Exposing the trade-off as two interchangeable implementations of the same `Theory`
interface lets the rest of the solver stay agnostic to the choice.

## What "indexed" actually means here: a RETE-inspired discrimination tree

The old documentation page for this topic (`clausedb-theories-and-RETE.md`) was never actually written, and the
name survives mostly as a promise: "clause storage uses RETE". That promise is *partially* accurate. The
current code does contain a package literally named
`it.unibo.tuprolog.collections.rete` (in the `:theory` module), and `IndexedTheory` is backed by it — so the
RETE terminology is not purely aspirational. What is worth being precise about is *which part* of RETE 2P-Kt
actually implements.

Classic RETE (Forgy, 1979) is a network for efficiently re-evaluating many production-rule conditions against a
working memory that changes incrementally, built from an **alpha network** (single-condition filters) feeding a
**beta network** (joins across conditions, with partial-match memories). 2P-Kt's `rete` package implements the
alpha-network idea — fast, incremental first-argument discrimination — and does **not** implement RETE's beta
network / join memories, because Prolog clause selection has no analogue of a multi-condition join: a goal
selects clauses by matching against a single term (the head), not by joining several partial patterns. So
calling this "a discrimination tree", "clause indexing", or "RETE-inspired indexing" is more accurate than
calling it "a RETE engine" outright; this page uses "RETE-style indexing" to keep the codebase's own naming
while being honest about scope.

The tree itself, `ReteTree`, is the engine behind `IndexedTheory`:

```kotlin
--8<-- "theory/src/commonMain/kotlin/it/unibo/tuprolog/collections/rete/custom/ReteTree.kt:10:33"
```

Its shape (see `it.unibo.tuprolog.collections.rete.custom.nodes`) is a chain of discrimination levels:

1. **`RootNode`** splits directives from rules right away (`DirectiveIndex` vs. `RuleNode`), since the two are
   never candidates for the same goal.
2. **`FunctorIndexingNode`** groups clauses by the functor of the goal's head — so a query for `parent(X, Y)`
   never even looks at `sibling/2` clauses.
3. **`ArityIndexing` / `FunctorIndexingNode`'s children** further split by arity (`nestedArity()`), since Prolog
   allows same-named predicates with different arities to coexist and be entirely unrelated (`foo/1` vs.
   `foo/2`).
4. Below that, **leaf-level indexes** (`AtomIndex`, `NumericIndex`, `CompoundIndex`, `VariableIndex`, under
   `rete/custom/leaf`) discriminate on the *first argument*'s shape, which is the cheapest and most common
   ISO/WAM-style indexing heuristic: variables can unify with anything and must always be considered, but atoms,
   numbers and compound terms of a specific shape can be bucketed and only re-checked against candidates that
   could possibly unify.

Every `assertA`/`assertZ`/`retractFirst`/`retractOnly`/`retractAll` operation walks down this same discrimination
path instead of touching the whole clause set, and `deepCopy()` gives `IndexedTheory` its immutable-update story
(the `add`/`retrieve` methods on `AbstractReteClauseCollection` copy the tree before mutating it). Note also the
`isOrdered` flag on `ReteTree`: the same indexing structure serves both order-preserving lookups (needed for
correct Prolog backtracking order) and order-agnostic ones, by changing how each node's cache is rebuilt rather
than by using a different data structure.

`ListedTheory`, by contrast, has no tree at all — `AbstractListedTheory` is backed directly by a `ClauseQueue`
holding clauses as an ordinary ordered sequence, and `get(clause)` degenerates to a linear scan with per-clause
unification checks. That is the price paid for its simplicity and cheap ordering guarantees.

## Two clause-collection shapes: queues and multisets

Independently of RETE-vs-listed, `:theory` distinguishes two general-purpose collection shapes, both built on
top of the same node structure:

- **`ClauseQueue`** preserves insertion order and exposes `addFirst`/`addLast` plus FIFO/LIFO-ordered retrieval —
  this is what backs Prolog's "clauses are tried in the order they were asserted" semantics.
- **`ClauseMultiSet`** drops ordering guarantees in exchange for a slightly cheaper `count`/`get` when many
  clauses can match a single query shape and their relative order does not matter.

`Theory` uses `ClauseQueue` (order matters for correct SLD resolution), but the queue/multiset split exists at
the `ClauseCollection` level because not every consumer of "a bunch of indexed clauses" is a knowledge base —
some are index buckets internal to the RETE tree itself.

## Why this design

The recurring theme is: **keep the Prolog-facing API (`Theory`) stable while letting the storage strategy vary
underneath it**. A solver only ever depends on `Theory`'s interface; whether a particular knowledge base is
indexed or listed, mutable or immutable, is a construction-time decision, not something the resolution engine
(see [Solver design](solver-design.md) and [The state-machine solver](state-machine.md)) needs to know about.
This mirrors the same "many implementations, one contract" pattern used for `Solver` itself
(`:solve-classic` vs. `:solve-streams`): 2P-Kt consistently prefers swappable implementations behind narrow
interfaces over one-size-fits-all data structures.
