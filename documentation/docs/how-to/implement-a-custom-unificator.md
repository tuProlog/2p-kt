# Implement a custom `Unificator`

How to change the way two `Term`s are compared during unification, and how to control occurs-check, using the
`unify` module's `Unificator` API.

This is a usage recipe; see the Explanation section for the rationale behind pluggable unification strategies.

## 1. Use a built-in strategy

`Unificator`'s companion object ships two ready-made strategies:

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:default"
```

- `Unificator.default` (an alias for `strict()`) compares terms with plain `Term.equals`.
- `Unificator.naive()` behaves like `strict()`, except it compares numbers by their numeric value rather than
  their exact representation (so `1` and `1.0` can be considered equal).

```kotlin
val strict = Unificator.strict()
val naive = Unificator.naive()

val substitution = strict.mgu(term1, term2)
```

Both factory methods accept an optional starting `context: Substitution` of pre-existing bindings to unify
against:

```kotlin
val context: Substitution = Substitution.of(Var.of("X") to Atom.of("a"))
val strictWithContext = Unificator.strict(context)
```

## 2. Disable occurs-check for a single call

`mgu`, `match`, and `unify` all accept an `occurCheckEnabled: Boolean` parameter (`true` by default):

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:operations"
```

```kotlin
val unificator = Unificator.default
val substitution = unificator.mgu(term1, term2, occurCheckEnabled = false)
```

The infix operators `mguWith`, `matches`, and `unifyWith` always perform occurs-check, since they delegate to
`Unificator.default`; use the explicit method calls above if you need to disable it.

## 3. Implement a custom unification strategy

Subclass `AbstractWorklistUnificator` and override `checkTermsEquality`, which decides whether two constants are
considered equal. For reference, this is how the built-in `naive()` strategy implements value-based comparison of
numbers:

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:naive"
```

Follow the same pattern for your own strategy — for instance, comparing atoms case-insensitively:

```kotlin
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractWorklistUnificator

val caseInsensitive =
    object : AbstractWorklistUnificator() {
        override fun checkTermsEquality(
            first: Term,
            second: Term,
        ): Boolean =
            when {
                first.isAtom && second.isAtom ->
                    first.castToAtom().value.equals(second.castToAtom().value, ignoreCase = true)
                else -> first == second
            }
    }

caseInsensitive.match(Atom.of("Foo"), Atom.of("foo")) // true
```

Since this strategy unifies atoms that differ (`Foo` and `foo`), it must also provide coarser index keys, or theories
using it would miss clauses: see [step 5](#5-keep-indexing-consistent).

`AbstractWorklistUnificator()` (no-arg) starts from an empty context; pass a `Substitution` to the constructor if you
need a starting context, same as the built-in factories.

To make unification depend on more than the structure of terms — e.g. on their tags — override `handleComparison`,
which sees every pair of terms about to be compared, left operand's side first, and may replace, accept or reject it:

```kotlin
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.UnificationStep

// a goal may only unify with clause heads whose terms carry (at least) the goal's tags
val tagAware =
    object : AbstractWorklistUnificator() {
        override fun handleComparison(step: UnificationStep) {
            if (!step.rhs.tags.entries.containsAll(step.lhs.tags.entries)) step.reject()
        }
    }
```

Do not extend the deprecated `AbstractUnificator`: it is a much slower, equation-based engine, only kept as a didactic
reference.

## 4. Add caching, if needed

Any `Unificator` — built-in or custom — can be wrapped to cache its results (LRU, capacity 32 by default):

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:cached"
```

```kotlin
val cached = Unificator.cached(caseInsensitive)
val smallerCache = Unificator.cached(caseInsensitive, capacity = 5)
```

Wrapping an already-cached `Unificator` again just re-wraps the original with the new capacity, instead of
double-caching.

## 5. Keep indexing consistent

Theories and clause collections built with your unificator (`Theory.of(unificator, clauses)`) select candidate clauses
through index keys before unifying them. The keys come from `Unificator.indexKey(term)`, and must satisfy: *if two
terms unify, either their keys are equal, or one of them is not exact* (see
[Index keys](../reference/unification-api.md#index-keys)).

- If your strategy only **rejects more** pairs than `strict()` does (as `tagAware` above), keep the default keys.
- If it **accepts** pairs with different principal symbols (different atoms, numbers of different types or values,
  structures with different functors), override `indexKey` so that such terms share a key. `caseInsensitive` keys
  atoms by their lower-case value:

```kotlin
--8<-- "theory/src/commonTest/kotlin/it/unibo/tuprolog/theory/TheoryUsageExamplesSpec.kt:case-insensitive"
```

- When in doubt, return `IndexKey.Wildcard` for the terms concerned: it disables indexing on them, which is always
  correct, only slower.

When keys change a clause head's principal symbol, stores scan clauses in order instead of selecting a literal
functor/arity group. Immutable retractions on this path rebuild the remaining store. Custom keys on arguments alone
still use the argument indexes.

`Unificator.cached(...)` delegates `indexKey` to the unificator it wraps.

## 6. Let solvers unify in their own bindings

Solvers that keep their own bindings (such as `:solve-prolog`'s, #1010) can unify goals with clauses
right in those bindings, without building a `Substitution` per unification, if the solver's unificator implements
`IncrementalUnificator`. The built-in strategies do; a custom one keeps working without it, on the slower path where the
solver calls `mgu` and copies the result.

To opt in, implement the interface and delegate to `unifyIncrementally`:

```kotlin
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.BindingStore
import it.unibo.tuprolog.unify.IncrementalUnificator
import it.unibo.tuprolog.unify.UnificationStep

class TagAwareUnificator :
    AbstractWorklistUnificator(),
    IncrementalUnificator {
    override fun handleComparison(step: UnificationStep) {
        if (!step.rhs.tags.entries.containsAll(step.lhs.tags.entries)) step.reject()
    }

    override fun unifyInto(
        store: BindingStore,
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ) = unifyIncrementally(store, term1, term2, occurCheckEnabled)
}
```

`checkTermsEquality`, `handleComparison` and `indexKey` behave the same on both paths. `handleResult`, which
post-processes the `Substitution` returned by `mgu` and `merge`, does not: `unifyInto` builds no `Substitution`, so it
never calls it. **Do not opt in if your unificator overrides `handleResult`**, or solvers would skip it. Without
opting in, such a unificator keeps working, as solvers then use `mgu`:

```kotlin
// fails every unifier binding a variable to a term tagged "forbidden": must stay on the mgu path
val noForbidden =
    object : AbstractWorklistUnificator() {
        override fun handleResult(
            request: UnificationRequest,
            result: Substitution,
        ): Substitution =
            if (result.isSuccess && result.values.any { "forbidden" in it.tags }) Substitution.failed() else result
    }
```

`Unificator.cached(...)` does not hide the opt-in: solvers unify in place with the unificator it wraps, if that one
implements `IncrementalUnificator`.
