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
--8<-- "unify/src/commonMain/kotlin/it/unibo/tuprolog/unify/Unificator.kt:operations"
```

- `mgu(term1, term2)` — computes the most general unifier, or `Substitution.failed()` if the terms don't unify;
- `match(term1, term2)` — `true` iff an MGU exists (shorthand for `mgu(...) !== Substitution.failed()`);
- `unify(term1, term2)` — applies the MGU to `term1` and returns the resulting `Term`, or `null` on failure.

A related operation, `merge(sub1, sub2, occurCheckEnabled)`, combines two `Substitution`s the same way `mgu` combines
two `Term`s: a variable bound by both gets its two values unified, rather than one overriding the other (unlike
`Substitution.plus`, which fails unless they are equal). Solvers use it to accumulate the bindings of a resolution.

Operands are not interchangeable in general: when they have distinct semantic roles (a goal vs. a rule head, e.g.),
the *subject* term should be passed first and the *reference* term second, since the returned substitution/unified
term can retain that orientation even though unifiability itself is symmetric.

### Creating unificators

The companion object provides three strategies, each obtainable with or without a starting `context: Substitution`:

- `Unificator.default` — an empty-context `strict()`, uses `Term.equals` for identity;
- `Unificator.strict(context = Substitution.empty())` — compares terms via `==`;
- `Unificator.naive(context = Substitution.empty())` — like `strict`, but compares `Integer`/`Numeric` terms by
  *value* rather than by type-and-value.

```kotlin
val strict = Unificator.strict()
val strictWithContext = Unificator.strict(someSubstitution)
```

### Caching

`Unificator.cached(other: Unificator, capacity: Int = 32)` decorates any `Unificator` with an LRU cache (via
`CachedUnificator`) that memoizes recent `mgu`/`match`/`unify` calls. `Unificator.default` performs no caching on its
own.

### How unification works

The built-in strategies are `AbstractWorklistUnificator`s: they keep a stack of pairs of terms still to unify and a
table of variable bindings. Each pair is popped, its variables are replaced by what they are currently bound to, and
then either a variable gets bound, or two compound terms with the same functor and arity are split into the pairs of
their arguments, or the unification fails. Bindings are recorded once and never propagated to the pending pairs; the
resulting `Substitution` is built once, at the end. Long lists and deep terms are processed iteratively.

With occurs-check disabled, cyclic bindings such as `X -> f(X)` are allowed, and unifying cyclic terms terminates. All
the state of an operation is local to it: a `Unificator` can be shared across threads.

`AbstractUnificator`, the equation-based (Martelli–Montanari style) engine used before, is deprecated: it is much slower,
and kept as a didactic reference only.

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

Implementing a custom `Unificator` amounts to subclassing `AbstractWorklistUnificator` and overriding any of:

- `checkTermsEquality(first: Term, second: Term): Boolean`, which decides whether two constants (or two variables) are
  equal — compound terms are compared argument by argument instead;
- `handleComparison(step: UnificationStep)`, invoked on every pair of terms about to be compared (compound ones
  included, before their decomposition): `step.lhs` always stems from the left operand and `step.rhs` from the right
  one, and the pair can be replaced (`step.replace(...)`, e.g. with tagged copies), accepted as equal
  (`step.accept()`) or rejected (`step.reject()`) — e.g. to take terms' tags into account, possibly asymmetrically;
- `handleResult(request: UnificationRequest, result: Substitution): Substitution`, invoked on each outcome.

```kotlin
val absoluteValueUnificator =
    object : AbstractWorklistUnificator() {
        override fun checkTermsEquality(first: Term, second: Term): Boolean = when {
            first is Integer && second is Integer ->
                first.value.absoluteValue.compareTo(second.value.absoluteValue) == 0
            else -> first == second
        }

        // 1 and -1 unify, so they must share an index key (see below)
        override fun indexKey(term: Term): IndexKey =
            if (term is Integer) IndexKey.OfInteger(term.value.absoluteValue) else IndexKey.strict(term)
    }

val sameTagsUnificator =
    object : AbstractWorklistUnificator() {
        override fun handleComparison(step: UnificationStep) {
            if (step.lhs.tags != step.rhs.tags) step.reject()
        }
    }
```

A custom strategy unifying terms with different principal symbols, like `absoluteValueUnificator`, must also override
`indexKey`, as explained next. `sameTagsUnificator` only rejects more pairs, so the default keys suit it.

### Index keys

Clause collections and theories (module `theory`) do not unify a query with every clause: they first narrow the
clauses down through indexes, by comparing *index keys*. `Unificator.indexKey(term): IndexKey` abstracts a term into
one of:

| `IndexKey` | For | Exact |
|---|---|---|
| `OfAtom(value)` | an atom | yes |
| `OfInteger(value)` | an integer | yes |
| `OfNumber(value)` | a real number, or any number for unificators comparing numbers by value | yes |
| `OfStruct(functor, arity)` | a compound term | yes |
| `AnyOfArity(arity)` | reserved: a compound term whose functor is a variable | no |
| `Wildcard` | a variable, or anything a unificator does not want indexed | no |

The contract mirrors the one between `hashCode` and `equals`: **if two terms unify, either their keys are equal, or
one of them is not exact**. Keys may merge terms that do not unify (indexing becomes less selective, never wrong), but
must never separate terms that do, or indexed lookups would miss clauses. The default implementation,
`IndexKey.strict(term)`, suits `strict()` and any unificator that only rejects more pairs (e.g. because of tags);
`naive()` uses `IndexKey.numbersByValue(term)`. Keys ignore tags. See
[Clause storage and indexing](../explanation/clause-storage-and-indexing.md) for how indexes use them.
