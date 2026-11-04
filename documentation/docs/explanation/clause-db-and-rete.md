# Clause Databases and Indexing

A Prolog engine spends most of its time doing one thing over and over: given a goal, find the clauses in the
knowledge base whose head *might* unify with it. Get this operation right and resolution is fast; get it wrong
and every call degenerates into a linear scan of the whole program. This page explains how 2P-Kt models
knowledge bases (`Theory`), the clause collections they belong to, and the indexing strategy behind clause
retrieval (redesigned in [#1019](https://github.com/tuProlog/2p-kt/issues/1019)).

## Theories are clause deques with a Prolog-shaped face

A knowledge base is a `Theory`: an ordered, taggable collection of clauses (facts, rules and directives),
queried by unification and updated with the ISO-flavoured vocabulary (`assertA`/`assertZ`/`retract`/`abolish`).
`Theory` is a `ClauseDeque`, one of the *clause collections* of the `it.unibo.tuprolog.collections` package:

| Collection | Order | Duplicates | Notes |
|---|---|---|---|
| `ClauseMultiSet` | none promised | allowed | equality ignores order |
| `ClauseSet` | none promised | no two variants | `add` skips variants of present clauses |
| `ClauseQueue` | Prolog order | allowed | `first()`; mutable: `addLast`, `removeFirst` |
| `ClauseDeque` | Prolog order | allowed | `last()`; mutable: `addFirst`, `removeLast` — `Theory` is one |
| `ClauseList` | Prolog order | allowed | positional access |
| `ClauseMap<V>` | key insertion | keys by variant | attaches data to clauses without touching their tags |

Each comes as a read-only interface with a `Mutable…` counterpart, mirroring Kotlin's collections:
`toMutable()` always copies, `toImmutable()` returns the very collection if it is immutable already.

All of them are *unification-aware*: membership comes in two flavours, both served by the same indexes.

- **Matching**: `get(query)`, `contains(query)`, `firstMatching`, `countMatching`, `removeFirstMatching`,
  `removeAllMatching` consider the clauses unifying with a query, according to the collection's `Unificator`.
  This is what Prolog means by "the clauses for this goal".
- **Identity**: `containsVariant`, `countVariants`, and the keys of sets and maps consider clauses *variants* of
  each other when they are equal up to a consistent renaming of variables (`Term.isVariantOf`), tags ignored.

Because of this, clause collections are not `kotlin.collections.Collection`s, whose `contains` and `equals`
would have to commit to one of the two notions. Collections are not taggable (theories are), but they preserve
clause tags, as they always return the very instances they were given.

## Two flavours of theory: read-optimised and write-optimised

- A plain **`Theory` is immutable** and serves **static knowledge bases**, which are loaded once and queried
  over and over. Its clauses live in arrays; every update returns a new theory that shares, with the original,
  every procedure (and every index) it does not touch.
- A **`MutableTheory` is updated in place** and serves **dynamic knowledge bases**, which `assert/1` and
  `retract/1` change on many resolution steps. Assertions and retractions take amortised constant time after
  lookup, and the clause count is maintained, not recomputed.

Solvers use exactly this split: their static KB is a `Theory`, their dynamic KB a `MutableTheory`.

## Indexing: procedures, then arguments, on demand

Both flavours share the same indexing scheme, drawn from the logic-programming literature rather than from
production-rule engines:

1. **Procedures.** Clauses are grouped by the functor and arity of their head (directives form one more group).
   A goal only ever looks at its own procedure, so `parent(X, Y)` never touches `sibling/2`, and the relative
   order of clauses only needs to be preserved within a procedure (iteration over the whole theory keeps the
   global order separately).
2. **Just-in-time argument indexes.** Within a procedure large enough to benefit, an index on argument *i* is
   built the first time a lookup binds argument *i*, as in YAP's demand-driven indexing (Costa, Sagonas and Lopes,
   ICLP 2007) and SWI-Prolog's JIT indexing. Each lookup then uses, among the arguments it binds, the index
   yielding the fewest candidates. So `p(X, a, Y)` is as fast as `p(a, X, Y)` whenever the second argument
   discriminates, which first-argument indexing (the WAM's `switch_on_term`) cannot offer.
3. **Candidates.** An index maps keys to the clauses having that key as argument; clauses with a variable there
   are candidates for every key. A lookup merges, in clause order, the clauses with the query's key and those with
   a variable, and unifies only those, lazily: asking for the first match stops at the first match.

Keys come from the unificator: `Unificator.indexKey(term)` abstracts a term into an `IndexKey` (an atom by value,
a number by value, a compound by functor and arity, a variable as a wildcard). The contract mirrors the one
between `hashCode` and `equals`: terms that unify must have equal keys, unless one of them is a wildcard. This is
what lets indexing honour each unificator's notion of equality: `Unificator.naive()`, which unifies `1` with
`1.0`, gives both the same key. A custom unificator that takes tags into account only to *reject* more pairs keeps
the default keys; one that *accepts* pairs with different principal symbols must override `indexKey` with coarser
keys, down to `IndexKey.Wildcard`, which disables indexing on that term. A reserved `IndexKey.AnyOfArity` keeps
room for future patterns, i.e. structures whose functor is a variable.

## The logical update view, for free

ISO Prolog prescribes the *logical update view* for dynamic predicates: a call sees the clauses as they were when
it started, whatever `assert/1` and `retract/1` do while it backtracks through them. `MutableTheory` implements it
with generations (Lindholm and O'Keefe, ICLP 1987): every update increments the theory's generation, every clause
records the generations it was added and removed in, and a lookup only yields the clauses alive at the generation
it started at. Retracted clauses are only marked, and physically dropped once they outnumber the live ones, so
storage stays proportional to the live clauses. Lookups and iterations are therefore lazy *and* safe while the
theory changes: the solver no longer copies the clauses of a dynamic predicate on every call.

## Why this design

Resolution needs one operation to be fast — "the clauses for this goal, in order" — and two kinds of knowledge
base with opposite update patterns. A single storage engine, with read- and write-optimised flavours behind the
same `Theory` contract, gives both without making the solver aware of the difference, and the same engine serves
every clause collection, so programmatic manipulation of logic programs gets the same indexing as resolution.
