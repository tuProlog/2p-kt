# Reuse logic variables with `Scope`

How to build terms that share the same logic variable in multiple places, using the `core` module's `Scope`
API — for example to construct the Prolog rule `member(H, [_|T]) :- member(H, T).`, where `H` and `T` each
occur twice and must refer to the *same* variable both times.

This is a usage recipe; see the Explanation section for why `Var` equality is scoped the way it is.

## 1. Know why you need this

Two `Var` instances created independently are never equal, even with the same name:

```kotlin
Var.of("X") == Var.of("X") // always false
```

To reuse a variable, you must keep a reference to the *same* `Var` instance and reuse that reference. Doing
this by hand with plain Kotlin `val`s works but gets cumbersome for anything beyond a couple of variables.

## 2. Create a `Scope` and build terms through it

`Scope` is a `Term` factory that remembers the variables it has already created, by name, and hands back the
same instance on a repeated request within that scope:

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Scope.kt:10:12"
```

![Scope interface](../assets/diagrams/scope.svg)

Use `Scope.empty { ... }` to build one or more terms inside a fresh scope, calling its factory methods
(`varOf`, `atomOf`, `structOf`, `consOf`, `ruleOf`, ...) instead of the static `Var.of`/`Struct.of`/etc.
equivalents:

```kotlin
import it.unibo.tuprolog.core.Scope

val memberRule = Scope.empty {
    ruleOf(
        structOf("member", varOf("H"), consOf(anonymous(), varOf("T"))),
        structOf("member", varOf("H"), varOf("T")),
    )
}
```

Every `varOf("H")` call within the same scope returns the *same* `Var` instance, so the two occurrences of
`H` (and of `T`) in the rule above are genuinely the same variable — unlike two independently-created
`Var.of("H")` instances.

## 3. Use `Scope.of` to seed a scope with pre-existing variables

If you already have some `Var`s (e.g. from a previously parsed term) and want further term-building to reuse
them by name, seed the scope with them instead of starting empty:

```kotlin
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var

val x = Var.of("X")
val scope = Scope.of(x)
scope.varOf("X") === x // true: same instance, retrieved by name
```

## 4. Inspect the variables collected by a scope

Every `Scope` exposes the variables it knows about as a `Map<String, Var>`:

```kotlin
Scope.empty {
    varOf("X")
    varOf("Y")
    variables // mapOf("X" to <Var X>, "Y" to <Var Y>)
}
```

## Notes

- `anonymous()` (and its alias `whatever()`) always creates a *fresh* anonymous variable, even within the same
  scope — unlike named variables, these are never reused. If you specifically want the same "don't care"
  variable reused across a scope, call `varOf("_")` instead: since it goes through the same by-name cache as
  any other named variable, repeated calls return the same instance.
- `Scope`s are mutable and meant to be short-lived: create a new one per logical unit of work (e.g. one per
  clause when building/parsing a theory), rather than reusing a single `Scope` across unrelated terms.
- `Term.freshCopy()` uses exactly this mechanism internally, creating a fresh `Scope` to consistently rename
  all variables in a term.
