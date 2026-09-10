# The Term Model

Every piece of data 2P-Kt manipulates — a Prolog atom, an integer, a variable, a compound structure, a
clause — is a [`Term`](https://github.com/tuProlog/2p-kt/blob/master/core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt).
This page is about *why* the term model looks the way it does; see [Term
hierarchy](../reference/term-hierarchy.md) for the full type-by-type API.

## The grammar behind the hierarchy

Logic terms are, at heart, the classical inductively-defined tree structures of first-order logic:

```text
Term     := Constant | Var | Struct
Constant := Atom | Numeric
Numeric  := Integer | Real
Struct   := Functor(Argument)
Argument := Term | Term, Argument
```

`Var`, `Atom`, and `Functor` are all, ultimately, strings drawn from some alphabet; 2P-Kt follows Prolog's
own convention for telling them apart syntactically (variables start with an uppercase letter or `_`, atoms
and functors are lowercase-initial or quoted). The `Term` type hierarchy is essentially this grammar
reified as Kotlin interfaces, plus a handful of pragmatic extensions — `List`, `Tuple`, and `Block` are all
just structures with a conventional functor and a folding convention for their arguments, `Clause` is a
structure with functor `:-`, and so on. Rather than inventing a dozen unrelated data types, 2P-Kt tries to
keep everything expressible as "a `Struct` with a well-known functor," so that generic code written against
`Struct` continues to work on `List`s, `Tuple`s, and clauses without special-casing.

One consequence worth calling out: the hierarchy is a **DAG, not a tree**. `Atom`, for instance, is both a
zero-arity `Struct` (it has a functor and no arguments) and a `Constant` (it is inherently ground and
carries a single value). Modelling it as a subtype of both, rather than picking one parent and duplicating
behaviour, keeps `isGround`/`value` reasoning and functor/arity reasoning both natively available on atoms
without any special-casing elsewhere in the codebase.

## Why terms are immutable

All terms in 2P-Kt are immutable: there is no public API that mutates a `Term` in place, only ones that
return a new one. This is not an incidental implementation detail — it is the property most of the rest of
the design leans on:

- **No aliasing bugs.** A `Term` handed to a solver, stored in a knowledge base, or captured in a closure
  can never be changed out from under its holder. Resolution and unification, which pass terms around
  extensively and often need to hold onto old versions of a term while computing a new one (see
  [Unification API](../reference/unification-api.md)), would be far harder to reason about otherwise.
- **Safe sharing and caching.** Because a term can never change, sub-terms can be shared between many larger
  terms with no risk, and singleton instances (empty lists, boolean atoms, etc.) can be cached and reused
  freely. Concurrent, OR-parallel resolution strategies (see `:solve-concurrent`) depend on exactly this
  property: no term is ever a shared mutable resource that needs locking.
- **"Editing" a term always means building a new one.** Applying a substitution, refreshing variables,
  appending an argument to a structure — all of these produce a fresh term rather than editing an existing
  one. The one place this has a real performance cost is deeply nested structures (long lists in
  particular), where naive copying would be wasteful; the list implementation deliberately defers as much
  of that copying as it can get away with, precisely because the immutability guarantee itself is
  non-negotiable.

## Three notions of equality, on purpose

A logic term can be compared to another in more than one meaningful sense, and 2P-Kt keeps those senses
distinct instead of picking one and calling it `equals`:

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/Term.kt:67:83"
```

- **Identity-like equality** (`==`, i.e. `equals(other: Any?)`) is the strictest: two variables are equal
  only if they share the same *complete* name — meaning they are, for all practical purposes, the exact
  same logic variable, not merely two variables that happen to be called the same thing. See [Variables and
  Scoping](variables-and-scoping.md) for why that distinction matters.
- **Equality with a choice** (`equals(other, useVarCompleteName)`) exists because sometimes what you want to
  compare is *shape*, ignoring which specific variable instances were used — e.g. to check that refreshing
  a clause produced "the same clause" up to variable renaming. Making this an explicit boolean parameter,
  rather than a second silently-different method, keeps both notions visible at the call site.
- **Structural equality** (`structurallyEquals`) goes one step further and treats *any* two variables as
  equal to each other, comparing only the term's shape (functor/arity/argument structure, numeric value).
  It answers "do these two terms have the same tree shape?" independent of naming entirely — useful for
  comparing term *templates* rather than specific logic statements.

Collapsing these three into one `equals` would force every caller to pick a side (usually the strictest
one) and would make the other, equally legitimate notions of "the same term" inexpressible without a
separate, ad-hoc comparator. Keeping three named operations makes the intended meaning explicit at each call
site instead of implicit in what code happens to have been written.

## A total order, for free

Every pair of terms in 2P-Kt is also comparable (`Term : Comparable<Term>`), following a standard
logic-term ordering: variables order before numbers, which order before atoms, which order before
structures (compared first by arity, then functor, then arguments left-to-right). This isn't an incidental
`Comparable` implementation bolted on for convenience — a total order on terms is what lets a knowledge base
index clauses, sort solutions deterministically, and implement ISO-standard comparison operators (`@<`,
`@=<`, ...) directly on top of the core term type, without every downstream module reinventing its own
ordering.

One place the term model has visibly moved on since its original design: an early iteration of the
collection types included a dedicated logic-set collection (`Set`/`EmptySet`, using `{}` as its functor).
The current core instead uses `Block` for curly-braced terms — closer to how `{}/1` is used in standard
Prolog (e.g. in DCG bodies) than to a general-purpose set collection. If you are reading older design notes
that mention a `Set` term type, take them as historical context rather than as describing the current API.
