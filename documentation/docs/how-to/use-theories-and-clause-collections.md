# Use theories and clause collections

Recipes for building, querying and updating knowledge bases (`Theory`, `MutableTheory`) and clause collections with
the `theory` module. Every snippet is quoted from `theory`'s `TheoryUsageExamplesSpec`, so it compiles and passes.
For the full list of operations, see [Theory and clause collections API](../reference/theory-api.md); for how they
work, see [Clause storage and indexing](../explanation/clause-storage-and-indexing.md).

## 1. Pick the right kind of theory

- Use a **`Theory`** for knowledge that is loaded once and queried many times (a program's static clauses). It is
  immutable and optimised for reads.
- Use a **`MutableTheory`** for knowledge that changes often (`assert/1`, `retract/1`, caches, working memories). It
  is updated in place and optimised for writes.

Both are indexed the same way, so lookups are equally selective; they differ in how updates behave.

## 2. Build and query a theory

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:static-theory"
```

- `theory[clause]` yields the clauses unifying with `clause`; `theory[head]` the rules whose head unifies with
  `head`, whatever their body; `theory[indicator]` the clauses of one procedure.
- Results are lazy `Sequence`s, in clause order: take only what you need (`firstOrNull()`, `take(n)`).
- Building clauses with variables? `Theory.of(unificator, { ... }, { ... })` runs each lambda in a fresh `Scope`, see
  [Reuse variables with Scope](reuse-variables-with-scope.md); the Kotlin DSL (`dsl-theory`) offers the same with
  less ceremony, see [Prolog DSL](../reference/prolog-dsl.md).

## 3. Update an immutable theory

Every update returns a new theory; keep the result. `retract` returns a `RetractResult` to pattern-match on:

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:immutable-updates"
```

To add many clauses, pass them all at once (`assertZ(listOf(...))`, `theory + otherTheory`): one update copies the
global clause array once, while many single-clause updates copy it once each.

## 4. Update a mutable theory, also while iterating

A `MutableTheory` changes in place. Its lookups follow the logical update view, so you can assert and retract while
consuming a lookup, without copying its results first:

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:mutable-theory"
```

## 5. Convert between the two

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:conversions"
```

Converting copies the clauses unless the theory already has the requested flavour. To freeze a mutable theory after a
loading phase, call `toImmutableTheory()` once, then share the result freely.

## 6. Check membership: unification or identity?

`contains` and `get` go by unification; `containsVariant` checks whether a clause is stored, up to variable renaming:

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:matching-vs-variants"
```

## 7. Use a plain clause collection

When you need clauses without the Prolog database vocabulary, pick the collection whose order and duplicate policy
fits: `ClauseList` (positional access), `ClauseQueue`/`ClauseDeque` (ends), `ClauseMultiSet` (no order),
`ClauseSet` (no two variants). Each has a `Mutable…` counterpart.

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:collections"
```

## 8. Attach data to clauses

To associate a probability, a provenance or a counter with each clause without changing its tags, use a `ClauseMap`.
Its keys are identified up to variable renaming, and `entriesMatching` finds entries by unification:

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:clause-map"
```

## 9. Unify candidates yourself

If your code unifies each retrieved clause anyway (e.g. a custom resolution engine computing the unifier it goes on
with), ask for `candidates` instead of `get`, and unify each clause once:

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:candidates"
```

## 10. Choose the unificator

The unificator decides both what unifies and how clauses are indexed. Pass it when building the theory, or change it
with `setUnificator` (which returns a new theory):

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:naive-keys"
```

With a custom unificator, make sure its index keys are consistent with it: see
[Implement a custom Unificator](implement-a-custom-unificator.md#5-keep-indexing-consistent).
