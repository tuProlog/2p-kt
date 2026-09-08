# Implement a custom `Unificator`

How to change the way two `Term`s are compared during unification, and how to control occurs-check, using the
`unify` module's `Unificator` API.

This is a usage recipe; see the Explanation section for the rationale behind pluggable unification strategies.

## 1. Use a built-in strategy

`Unificator`'s companion object ships two ready-made strategies:

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:92:94"
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
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:23:29"
```

```kotlin
val unificator = Unificator.default
val substitution = unificator.mgu(term1, term2, occurCheckEnabled = false)
```

The infix operators `mguWith`, `matches`, and `unifyWith` always perform occurs-check, since they delegate to
`Unificator.default`; use the explicit method calls above if you need to disable it.

## 3. Implement a custom unification strategy

Subclass `AbstractUnificator` and override `checkTermsEquality`, which decides whether two terms are
considered equal while building the unification equations. For reference, this is how the built-in `naive()`
strategy implements value-based comparison of numbers:

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:117:133"
```

Follow the same pattern for your own strategy — for instance, comparing atoms case-insensitively:

```kotlin
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractUnificator

val caseInsensitive =
    object : AbstractUnificator() {
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

`AbstractUnificator()` (no-arg) starts from an empty context; pass a `Substitution` to the constructor if you
need a starting context, same as the built-in factories.

## 4. Add caching, if needed

Any `Unificator` — built-in or custom — can be wrapped to cache its results (LRU, capacity 32 by default):

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:165:175"
```

```kotlin
val cached = Unificator.cached(caseInsensitive)
val smallerCache = Unificator.cached(caseInsensitive, capacity = 5)
```

Wrapping an already-cached `Unificator` again just re-wraps the original with the new capacity, instead of
double-caching.
