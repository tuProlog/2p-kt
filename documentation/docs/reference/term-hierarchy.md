# Term hierarchy

The `:core` module defines 2P-Kt's representation of logic data: `Term`s. This page is a precise, per-type
description of that API, grounded in `core/src/commonMain/kotlin/it/unibo/tuprolog/core/`. See
[Term model](../explanation/term-model.md) for the design rationale (why terms are immutable, identity vs.
structural equality, and so on) — this page sticks to *what* the API is.

Logic terms can be defined by the following context-free grammar (non-terminals in upper case):

|            |      |                                                            |
|-----------:|:----:|------------------------------------------------------------|
|     `Term` | `:=` | `Constant \| Var \| Struct`                                |
| `Constant` | `:=` | `Atom \| Numeric`                                          |
|  `Numeric` | `:=` | `Integer \| Real`                                          |
|   `Struct` | `:=` | `Functor(Argument, ...)`                                   |
|      `Var` | `:=` | strings matching `[A-Z_][A-Za-z_0-9]*`                     |
|     `Atom` | `:=` | strings matching `[a-z][A-Za-z_0-9]*`, or quoted strings    |
|  `Functor` | `:=` | a (non-quoted) atom, or an operator symbol                 |

For example, `f(X, y, g(1, 2.3), h(_, 'j k'))` is a `Struct` with functor `f` and 4 arguments: the variable `X`,
the atom `y`, the structure `g(1, 2.3)` (functor `g`, arguments the integer `1` and the real `2.3`), and the
structure `h(_, 'j k')` (arguments the anonymous variable `_` and the quoted atom `'j k'`).

![terms-nofields class diagram](../assets/diagrams/terms-nofields.svg)

!!! note "Diagram note"
    This diagram (migrated from 2P-Kt's older documentation) still labels the `{}`-functored term type `Set` /
    `EmptySet`. In current `core`, that type is named [`Block`/`EmptyBlock`](#blocks) instead — see the
    [Blocks](#blocks) section below. Every other type shown is up to date.

## `Term`

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt:11:16"
```

![Term interface](../assets/diagrams/term.svg)

`Term` is the root of the hierarchy. It is `Comparable<Term>` (a total, implementation-defined [standard order of
terms](https://www.swi-prolog.org/pldoc/man?section=standardorder), via `TermComparator.DefaultComparator`),
`Taggable<Term>` (attach arbitrary `tags: Map<String, Any>` to a term, preserved across most transformations),
`Castable<Term>` (`as<T>()` / `castTo<T>()` fluent down-casts) and `Variabled` (`variables: Sequence<Var>`,
`isGround: Boolean`).

Terms are **immutable**: no subtype exposes a `var` property or a side-effecting method. All apparent "mutators"
(e.g. `Struct.setArgs`, `Clause.setBody`) instead return a *new* term, leaving the receiver untouched.

Key members:

- `variables: Sequence<Var>` / `isGround: Boolean` — from `Variabled`.
- `freshCopy(): Term` / `freshCopy(scope: Scope): Term` — from `Applicable<Term>`; returns a structurally-equal
  term with all contained variables consistently renamed (same variable name ⇒ same renaming), reusing `scope`'s
  variables when one is given.
- `apply(substitution: Substitution): Term` (and the `term[substitution]` operator alias) — from `Applicable<Term>`;
  replaces variables according to a `Substitution`.
- `equals(other: Term, useVarCompleteName: Boolean): Boolean` — identity check for which the caller decides whether
  two `Var`s are compared by `completeName` (default `equals(Any?)` behavior) or just by `name`.
- `structurallyEquals(other: Term): Boolean` — a looser equivalence: all variables are mutually equal, and numbers
  compare by value (`1 structurallyEquals 1.0`).
- `accept(visitor: TermVisitor<T>): T` — double-dispatch entry point for the visitor pattern (`TermVisitor<T>`).
- A family of `isXxx: Boolean` flags (`isVar`, `isStruct`, `isAtom`, `isNumber`, `isList`, `isTuple`, `isBlock`,
  `isClause`, `isRule`, `isFact`, `isDirective`, `isIndicator`, `isTrue`, `isFail`, ...), each guaranteed `true` iff
  the term is an instance of the corresponding subtype.
- A matching family of `asXxx(): Xxx?` (nullable down-cast) and `castToXxx(): Xxx` (throwing down-cast) methods, one
  per subtype, e.g. `asAtom()`/`castToAtom()`, `asStruct()`/`castToStruct()`, `asRule()`/`castToRule()`.

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt:39:83"
```

There is no way to instantiate a bare `Term`: only its subtypes have public factory methods (documented below), or
terms are built via a [`Scope`](../how-to/reuse-variables-with-scope.md), which also lets several terms share
`Var` instances.

`Term.toString()` produces a raw, debug-oriented representation (not meant for end-user display): compound terms
in canonical `functor(arg1, ..., argN)` form (quoting the functor when necessary), except lists (`[a, b]`), blocks
(`{a, b}`), tuples (`(a, b)`), and clauses (`Head :- Body`, or `:- Body` for directives).

## `Var`

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Var.kt:8:33"
```

![Var interface](../assets/diagrams/var.svg)

`Var`s are placeholders for other terms. Each has a *complete name* `<name>_<id>`, where `name` is the (simple,
user-facing) `name: String` and `id: String` is an implementation-chosen suffix guaranteed to make `completeName`
globally unique; two variables are `equals()` only if their complete names match.

- `isAnonymous: Boolean` — `true` iff `name == "_"`.
- `isNameWellFormed: Boolean` — `true` iff `name` matches `[A-Z_][A-Za-z_0-9]*` (`Var.NAME_PATTERN`). Non-well-formed
  names can still be instantiated; they are printed back-quoted (`` `name`_id ``).
- `Var.of(name: String): Var` and `Var.anonymous(): Var` are the only two factories; both always mint a fresh,
  never-before-seen variable — callers have no control over `id`. To *reuse* a variable, hold on to the `Var`
  instance, or use a [`Scope`](../how-to/reuse-variables-with-scope.md).

## `Constant`

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Constant.kt:5:15"
```

![Constant interface](../assets/diagrams/constant.svg)

`Constant`s are ground, non-compound terms characterized by a `value: Any`. They cannot be instantiated directly —
only through the `Atom`/`Numeric` factories below.

## `Numeric`

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Numeric.kt:9:29"
```

![Numeric interface](../assets/diagrams/numeric.svg)

`Numeric` is either an `Integer` or a `Real`. Numbers are backed by the arbitrary-precision, Kotlin-multiplatform
`org.gciatto.kt.math.BigInteger`/`BigDecimal` (not `java.math`, so the same code runs on JVM and JS): `intValue`
and `decimalValue` expose both views regardless of the concrete subtype, and `compareValueTo(other)` compares by
numeric value (so `Numeric` also gets `<`, `<=`, `>`, `>=` via Kotlin operator overloading). `value: Any` returns
whichever of `BigInteger`/`BigDecimal` is the natural representation.

Factories: `Numeric.of(value: Number)`, `Numeric.of(value: String)` (tries `Integer.of` first, falls back to
`Real.of`), plus the type-specific `Integer.of(...)` (from `Int`/`Long`/`Short`/`Byte`/`BigInteger`/`BigDecimal`,
or `String` with an optional `radix`) and `Real.of(...)` (from `Float`/`Double`/`BigDecimal`/`String`).

## `Struct`

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Struct.kt:132:198"
```

![Struct interface](../assets/diagrams/struct.svg)

`Struct`s are compound terms: a `functor: String` plus an ordered list of `args: List<Term>` (`arity: Int` of
them). Every non-`Var`, non-`Numeric` term in 2P-Kt — atoms, lists, tuples, blocks, indicators, clauses — is
ultimately a `Struct`. Besides `setArgs`/`setFunctor` (which, like every "setter" in the hierarchy, return a new
`Struct` rather than mutating), `Struct` also has `append`/`addFirst`/`addLast`/`insertAt` to build a new `Struct`
with one extra argument. `indicator: Indicator` is a shorthand for `Indicator.of(functor, arity)`. Construct one
with `Struct.of(functor: String, vararg args: Term)`.

## `Atom`

![Atom interface](../assets/diagrams/atom.svg)

`Atom`s are `Struct`s of arity 0 that are simultaneously `Constant`s: `value: String` and `functor` always
coincide. They are how bare strings/symbols are represented. `Atom.of(value: String)` is the factory — note that
it interns a few well-known values to specific subtypes (`"[]"` → `EmptyList`, `"{}"` → `EmptyBlock`, `"true"` /
`"fail"` / `"false"` → `Truth`).

## `Truth`

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Truth.kt:8:21"
```

![Truth interface](../assets/diagrams/truth.svg)

`Truth` is the `Atom` subtype for the three boolean-ish atoms ISO Prolog treats specially: `Truth.TRUE` (`"true"`),
`Truth.FAIL` (`"fail"`), and `Truth.FALSE` (`"false"`) — the latter two both count as `isFail`. `Truth.of(Boolean)`
maps `true`/`false` to `TRUE`/`FALSE`.

## `Indicator`

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Indicator.kt:18:45"
```

![Indicator interface](../assets/diagrams/indicator.svg)

An `Indicator` denotes a predicate/functor by name and arity, e.g. `foo/2`: a `Struct` with functor `/` and two
arguments, `nameTerm`/`arityTerm` (kept as generic `Term`s so a partially-instantiated indicator can exist).
`isWellFormed` holds when `nameTerm` is an `Atom` and `arityTerm` a non-negative `Integer`, in which case
`indicatedName: String?`/`indicatedArity: Int?` extract the concrete values. Build one via `Indicator.of(name:
String, arity: Int)` or the generic `Indicator.of(name: Term, arity: Term)`.

## Collections

`List`, `Tuple`, and `Block` are the three "collection-like" `Struct`s. All three implement `Recursive`, which
gives them a uniform way to view their elements:

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Recursive.kt:6:41"
```

### Lists

![List interface](../assets/diagrams/list.svg)

A `List` is either an `EmptyList` (the atom `[]`) or a `Cons` — a `Struct` with functor `.` and 2 arguments,
`head: Term` and `tail: Term`, usually written `[Head | Tail]`. A `List` is `isWellFormed` when it is `[]`- 
terminated (a well-formed, `Cons`-chained list prints without a `|`, e.g. `[1, b, 3]`; a non-well-formed one prints
`[1, b | T]`). Factories: `List.of(vararg items)`, `List.of(items: Iterable<Term>)`, `List.from(items, last)` (to
build an explicitly `last`-terminated, possibly non-well-formed list), `Cons.of(head, tail)`,
`Cons.singleton(head)`, and `List.empty()` / `Empty.list()`.

### Tuples

![Tuple interface](../assets/diagrams/tuple.svg)

A `Tuple` is a `Struct` with functor `,` and 2 arguments, `left`/`right`, usually written `(Left, Right)`. When
`right` is itself a `Tuple`, the whole chain prints as one comma-separated, parenthesized sequence, e.g.
`(a, 2, c)` for `','(a, ','(2, c))`. Tuples always have 2 or more elements — there is no empty or singleton tuple.
Build one with `Tuple.of(left, right)`, `Tuple.of(items: Iterable<Term>)` (requires at least 2), or
`Tuple.wrapIfNeeded(...)`, which collapses to the single element itself (or a caller-supplied fallback) when fewer
than 2 items are given.

### Blocks

A `Block` is a `Struct` with functor `{}` and either 0 arguments (`EmptyBlock`, the atom `{}`) or exactly 1
argument (usually itself a `Tuple`, when the block groups several terms), usually written `{Argument}`. This is
2P-Kt's representation of Prolog's curly-brace term (`'{}'/1`), e.g. `{a, 2, c}` for `'{}'(','(a, ','(2, c)))`.
Build one with `Block.of(vararg terms)` / `Block.of(terms: Iterable<Term>)` (collapsing to `EmptyBlock` for zero
terms, or wrapping 2+ terms in a `Tuple`), or `Block.empty()` / `Empty.block()`.

!!! note
    Earlier versions of this documentation (and the overview diagram near the top of this page) call this type
    `Set`/`EmptySet`. It is *not* set-semantics (no deduplication) — it is a generic curly-brace grouping term, and
    the current source names it `Block`/`EmptyBlock` (`it.unibo.tuprolog.core.Block`).

## Clauses

`Clause`, `Rule`, `Fact`, and `Directive` represent [Horn clauses](https://en.wikipedia.org/wiki/Horn_clause). All
share functor `:-`:

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Clause.kt:7:44"
```

![Clause interface](../assets/diagrams/clause.svg)

- A **`Directive`** has no head: `':-'(Body)` (`head == null`, arity 1), printed `:- Body`.

  ![Directive interface](../assets/diagrams/directive.svg)

- A **`Rule`** has a head: `':-'(Head, Body)` (arity 2), printed `Head :- Body`. `head: Struct` is non-nullable on
  `Rule` (narrowing `Clause.head: Struct?`).

  ![Rule interface](../assets/diagrams/rule.svg)

- A **`Fact`** is a `Rule` whose body is `true`: `':-'(Head, true)`, printed just as `Head`. `Fact.of(head: Struct)`
  is the factory; `body` is fixed to `Truth.TRUE`.

  ![Fact interface](../assets/diagrams/fact.svg)

`Clause.isWellFormed` additionally checks that neither the head nor the body (nor its `,`/`;`/`->`-connected
sub-terms) contain a bare `Numeric` where a goal/argument is expected. Beyond `head`/`body`, `Clause` (and its
`Rule`/`Fact` narrowings) expose a family of head/body "wither" methods that return a new clause rather than
mutating — `setHead`/`setBody`, `setHeadArgs`/`setBodyItems`, `insertHeadArg`/`insertBodyItem`,
`add{First,Last}HeadArg`/`add{First,Last}BodyItem` — plus `bodyItems`/`bodySize`/`bodyAsTuple`/`getBodyItem(index)`
to navigate a (possibly `Tuple`-shaped) body without manual unwrapping. Build clauses with `Clause.of(head?, vararg
body)`, `Rule.of(head, vararg body)`, or `Directive.of(vararg body)`.
