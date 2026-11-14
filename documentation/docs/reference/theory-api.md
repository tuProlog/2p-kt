# Theory and clause collections API

The `theory` module (`it.unibo.tuprolog.theory`, `it.unibo.tuprolog.collections`) provides in-memory knowledge bases
(`Theory`, `MutableTheory`) and a family of unification-aware clause collections. This page lists what the API offers
and the semantics each operation commits to. For how it is implemented, see
[Clause storage and indexing](../explanation/clause-storage-and-indexing.md); for recipes, see
[Use theories and clause collections](../how-to/use-theories-and-clause-collections.md); for every signature, see the
[API reference](https://tuprolog.github.io/2p-kt/api/).

All code samples on this page are quoted from `theory`'s `TheoryUsageExamplesSpec`, so they compile and pass.

## Two notions of membership

Every clause collection is *unification-aware*: it is parameterised by a `Unificator`, and offers two notions of
membership, both served by the same indexes.

| Notion | Operations | A clause counts if… |
|---|---|---|
| Matching | `get(query)`, `contains(query)`, `firstMatching`, `countMatching`, `candidates`, `removeFirstMatching`, `removeAllMatching` | it unifies with the query, according to the collection's unificator |
| Identity | `containsVariant`, `countVariants`, keys of sets and maps | it is a *variant* of the given clause: equal up to a consistent renaming of variables (`Term.isVariantOf`), tags ignored |

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:matching-vs-variants"
```

For this reason clause collections are not `kotlin.collections.Collection`s, whose `contains` and `equals` would have
to commit to one of the two notions. Every collection returns the very `Clause` instances it was given, so clause
tags are preserved.

`candidates(query)` returns the clauses an index selects for the query, *without unifying them*: a superset of
`get(query)`, in the same order. It is meant for callers that unify each clause themselves anyway, such as solvers.

## The collection family

Each read-only interface has a `Mutable…` counterpart, mirroring Kotlin's collections.

| Interface | Order | Duplicates | Equality | Extra operations |
|---|---|---|---|---|
| `ClauseCollection` | — | — | — | the root: lookups, `size`, `rules`, `directives`, `toMutable`, `toImmutable` |
| `ClauseMultiSet` | none promised | allowed | same clauses, same multiplicities | — |
| `ClauseSet` | none promised | no two variants | as multisets | `add` returns `false` for a variant of a present clause |
| `ClauseQueue` | Prolog order | allowed | same clauses, same order | `first`, `firstOrNull`; mutable: `addLast`, `removeFirst` |
| `ClauseDeque` | Prolog order | allowed | as queues | `last`, `lastOrNull`; mutable: `addFirst`, `removeLast` |
| `ClauseList` | Prolog order | allowed | as queues | `get(index)`, `indexOfFirstMatching`, `asList`; mutable: `add(index, c)`, `set`, `removeAt` |
| `Theory` | Prolog order | allowed | same clauses, same order, only with another `Theory` | see below |
| `ClauseMap<V>` | key insertion | keys by variant | same keys (by `equals`) and values | `get(key)`, `entriesMatching(query)`, `asMap`; mutable: `put`, `remove`, `removeAllMatching` |

Every interface has companion factories: `empty(unificator = Unificator.default)` and `of(unificator, clauses)`, taking
a vararg, an `Iterable` or a `Sequence`. `ClauseSet.of` skips clauses that are variants of earlier ones;
`ClauseMap.of` keeps, for variant keys, the first key and the last value.

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:collections"
```

`ClauseMap` attaches data to clauses without touching their tags:

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:clause-map"
```

### Conversions

| Call | On an immutable collection | On a mutable collection |
|---|---|---|
| `toImmutable()` | `this` | a copy |
| `toMutable()` | a copy | a copy |
| `Theory.toImmutableTheory()` | `this` | a copy |
| `Theory.toMutableTheory()` | a copy | `this` |

### Mutable collections and the logical update view

Lookups and iterations of every mutable collection follow the *logical update view*: they see the clauses as of when
they started, whatever happens to the collection while they are consumed. It is safe to update a collection while
iterating over one of its lookups, without copying the results first.

## `Theory`

A `Theory` is a `ClauseDeque` and a `Taggable<Theory>`, with the ISO-flavoured vocabulary of Prolog databases.

| Operation | Semantics |
|---|---|
| `clauses`, `rules`, `directives` | all clauses / rules (facts included) / directives, in order |
| `get(clause)` | the clauses unifying with `clause`, lazily, in order |
| `get(head: Compound)` | the rules whose head unifies with `head`, whatever their body |
| `get(indicator)` | the rules of procedure `indicator` (e.g. `parent/2`); throws `IllegalArgumentException` if not well formed |
| `contains(clause / head / indicator)` | whether the corresponding `get` yields anything |
| `assertA(clause)`, `assertZ(clause)` | the clause added at the beginning / end; also on a `Struct` (a fact), an `Iterable` or a `Sequence` |
| `plus(clause)`, `plus(theory)` | same as `assertZ`; `plus(theory)` keeps the receiver's unificator and tags |
| `retract(clause)` | removes the first clause unifying with `clause`; on a `Struct`, the first rule whose head unifies |
| `retract(clauses)` | removes the first clause unifying with each pattern, in order: succeeds if at least one did |
| `retractAll(clause / head)` | removes every clause unifying with `clause` / every rule whose head unifies with `head` |
| `abolish(indicator)` | removes every clause of a procedure |
| `setUnificator(unificator)` | the same clauses, matched (and indexed) by another unificator: `this` if equal to the current one |
| `toString(asPrologText = true)` | the clauses as Prolog source, each terminated by `.` and a newline |
| `equals(other, useVarCompleteName)` | same clauses in the same order; variables compared by complete name, or by name only |

Clauses whose heads are [`Pattern`s](term-hierarchy.md#pattern), i.e. *metarules* such as `P(X) :- Q(X)`, belong to
every procedure of their arity: `get` and `contains` on `p(a)` see them, in their place in the theory, whereas
`get(indicator)` and `abolish(indicator)` concern concrete indicators, and so concrete clauses only. Removals by a
head with a name (`retract(p(X))`, `retractAll(p(X))`, `abolish(p/1)`) remove concrete clauses only, never
metarules; removals by a pattern head (`retract(P(X))`) remove whatever unifies with it, metarules included.

Every update rejects clauses that are not well formed (`Clause.isWellFormed`, e.g. a number as a clause head) with an
`IllegalArgumentException`. Retractions return a `RetractResult`: a `Success`, carrying the resulting `theory` and the
removed `clauses` (`firstClause`, the first of them), or a `Failure`, carrying the theory unchanged.

The factories are `Theory.empty(unificator)`, `Theory.of(unificator, clauses)` (vararg, `Iterable`, `Sequence`, or
`Scope.() -> Clause` lambdas, each run in a fresh `Scope`), and the same without the unificator, which then is
`Unificator.default`. `indexedOf` and `emptyIndexed` are synonyms kept for compatibility: every theory is indexed.
`TheoryFactory` (default implementation: `IndexedTheoryFactory`) abstracts these factories, for code that builds
theories without hard-coding how.

### Immutable theories

A plain `Theory` is immutable: every update returns a new theory, sharing with the original every procedure it does
not touch, or the original itself when nothing changes.

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:immutable-updates"
```

### Mutable theories

A `MutableTheory` updates itself in place and returns `this` from every update (`RetractResult.theory` is `this` too).
It is also a `MutableClauseDeque`: `add`/`addLast` mean `assertZ`, `addFirst` means `assertA`, `removeFirstMatching`
means `retract`. `setUnificator` (with a different unificator), `clone` and `toMutable` return independent copies.

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:mutable-theory"
```

## Complexity

With *n* clauses in the collection, *k* in the procedure a query targets, and *c* candidates selected by its index:

| Operation | `Theory` / immutable collections | `MutableTheory` / mutable collections |
|---|---|---|
| lookup (`get`, `candidates`), first result | O(c) (plus a one-off O(k) to build a missing index) | O(c) amortised (same) |
| `assertA`/`assertZ` of one clause | O(n + k) (copies the global array and one procedure) | amortised O(1) per built index |
| `retract` of one clause | O(n + k) after lookup | amortised O(1) after lookup |
| `size` | O(1) | O(1) |
| positional access (`ClauseList.get`) | O(1) | O(n) |
| `equals`, `hashCode` | O(n) (`Theory.hashCode` is cached) | O(n) |

Procedures with fewer than 4 clauses are scanned (*c* = *k*). Lookups and iterations are lazy: they cost only what
is consumed.

## Index keys

Indexing relies on `Unificator.indexKey(term): IndexKey`, under the contract *if two terms unify, either their keys
are equal, or one of them is not exact*. The built-in unificators provide matching keys (`Unificator.naive()` keys
numbers by value); see the [Unification API](unification-api.md#index-keys) and
[Implement a custom Unificator](../how-to/implement-a-custom-unificator.md#5-keep-indexing-consistent).

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:naive-keys"
```
