# Unification API

Unification is the process of matching two [`Term`](../reference/index.md)s by computing a substitution of their
variables that makes them syntactically equal — the *most general unifier* (MGU). 2P-Kt exposes this as a standalone
API in the `:unify` module, independent of the resolution engine.

## `Substitution`

A `Substitution` (package `it.unibo.tuprolog.core`) represents a set of variable bindings. It is a `Map<Var, Term>`
and comes in exactly two flavors, modeled as a sealed interface:

- `Substitution.Unifier` — a successful substitution, actually binding zero or more `Var`s to `Term`s;
- `Substitution.Fail` — the (singleton) representation of a failed unification attempt; it binds nothing.

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Substitution.kt:17:28"
```

Key members:

- `isSuccess` / `isFailed` — discriminate between the two cases without casting;
- `asUnifier()` / `castToUnifier()` and `asFail()` / `castToFail()` — safe/unsafe downcasts;
- `applyTo(term: Term): Term?` — applies the bindings to a `Term`, returning `null` on `Fail`;
- `plus(other: Substitution): Substitution` — composes (unions) two substitutions; the result is `Fail` if either
  operand is `Fail` or if the union is contradictory (same `Var` bound to two different `Term`s);
- `minus(...)` / `filter(...)` — remove or select entries, preserving the concrete subtype (`Unifier` stays
  `Unifier`, `Fail` stays `Fail`);
- `getOriginal(variable: Var): Var?` — walks a chain of bindings backwards to find the original variable name.

Construction goes through `Substitution`'s companion object: `Substitution.empty()`, `Substitution.failed()`,
`Substitution.of(variable, term)`, `Substitution.of(vararg pairs)`, `Substitution.unifier(...)` (same as `of` but
throws `SubstitutionException` instead of returning `Fail` on contradiction), and their `Map`/`Iterable`/`Sequence`
overloads.

## `Unificator`

The `Unificator` interface (`it.unibo.tuprolog.unify.Unificator`) is the entry point for performing unification. It
carries a `context: Substitution` — pre-existing bindings assumed while unifying — and exposes three core operations,
each with an `occurCheckEnabled` toggle (default `true`):

```kotlin
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:23:70"
```

- `mgu(term1, term2)` — computes the most general unifier, or `Substitution.failed()` if the terms don't unify;
- `match(term1, term2)` — `true` iff an MGU exists (shorthand for `mgu(...) !== Substitution.failed()`);
- `unify(term1, term2)` — applies the MGU to `term1` and returns the resulting `Term`, or `null` on failure.

A related operation, `merge(sub1, sub2, occurCheckEnabled)`, combines two `Substitution`s the same way `mgu` combines
two `Term`s (used internally when composing partial unifiers).

Operands are not interchangeable in general: when they have distinct semantic roles (a goal vs. a rule head, e.g.),
the *subject* term should be passed first and the *reference* term second, since the returned substitution/unified
term can retain that orientation even though unifiability itself is symmetric.

### Creating unificators

The companion object provides three strategies, each obtainable with or without a starting `context: Substitution`:

- `Unificator.default` — `strict()` wrapped nowhere further; uses `Term.equals` for identity;
- `Unificator.strict(context = Substitution.empty())` — compares terms via `==`;
- `Unificator.naive(context = Substitution.empty())` — like `strict`, but compares `Integer`/`Numeric` terms by
  *value* rather than by type-and-value.

```kotlin
val strict = Unificator.strict()
val strictWithContext = Unificator.strict(someSubstitution)
```

### Caching

`Unificator.cached(other: Unificator, capacity: Int = 32)` decorates any `Unificator` with an LRU cache (via
`CachedUnificator`) that memoizes recent `mgu`/`match`/`unify` calls. `Unificator.default` already uses an
internal LRU cache of `DEFAULT_CACHE_CAPACITY` (32) entries.

### Infix operators

For lighter-weight call sites, the companion object also defines infix extension functions on `Term` and
`Substitution`, all backed by `Unificator.default` (hence always occur-check-enabled):

```kotlin
val substitution = term1 mguWith term2
val matches = term1 matches term2
val unified = term1 unifyWith term2
val merged = substitution1 mergeWith substitution2
```

### Custom unification strategies

Implementing a custom `Unificator` amounts to subclassing `AbstractUnificator` and overriding
`checkTermsEquality(first: Term, second: Term): Boolean`, the primitive used throughout the unification algorithm to
decide whether two non-variable terms are equal:

```kotlin
val absoluteValueUnificator =
    object : AbstractUnificator() {
        override fun checkTermsEquality(first: Term, second: Term): Boolean = when {
            first is Integer && second is Integer ->
                first.value.absoluteValue.compareTo(second.value.absoluteValue) == 0
            else -> first == second
        }
    }
```
