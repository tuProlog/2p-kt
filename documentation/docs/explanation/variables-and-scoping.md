# Variables and Scoping

Logic variables look deceptively simple — a variable is just a name, like `X` — but naming is exactly where
the subtlety lives. This page explains why 2P-Kt's `Var` behaves the way it does, and what problem the
`Scope` abstraction exists to solve. For the mechanics of using `Scope` and unification day-to-day, see
[Unification API](../reference/unification-api.md).

## Why two variables named "X" are never equal

In 2P-Kt, this is always `false`:

```kotlin
Var.of("X") == Var.of("X")
```

That looks wrong until you consider what a Prolog variable actually *is*: a placeholder scoped to one
particular occurrence in one particular clause (or query). Two occurrences of `X` in two unrelated clauses
are, logically, two entirely different variables that just happen to share a human-readable name — the same
way two different methods can both have a local variable called `x` without those two `x`s being "the same
variable." If `Var.of("X")` produced interchangeable variables, resolution would have no way to tell those
occurrences apart, and every clause containing `X` would accidentally alias every other clause containing
an `X`, which would silently break unification the moment two clauses using the same variable names were
combined.

2P-Kt solves this by making variable identity independent of the simple name entirely. Internally, every
`Var` carries a *complete name* — the simple name plus a per-name sequential identifier chosen behind the
scenes (`X` becomes `X_1`, the next unrelated `X` becomes `X_2`, and so on) — and `==` compares complete
names. `Var.of` and `Var.anonymous()` are the only ways to create a variable, and neither one lets the
caller pick the identifier, precisely so no code can accidentally "forge" a collision. This is deliberate,
not an oversight: a factory that returns "the same" variable for the same name would be *wrong* far more
often than it would be convenient — see [Term hierarchy](../reference/term-hierarchy.md) for the identity
vs. equality distinction this feeds into, and [Term Model](term-model.md) for how `equals` exposes both a
complete-name and a simple-name comparison mode for exactly this reason.

## The problem this creates, and what `Scope` is for

Taken alone, the rule above has an awkward consequence: if you *do* want the same variable to occur twice
(as in `member(H, [_|T]) :- member(H, T).`, where `H` and `T` each occur twice), calling `Var.of("H")` twice
gives you two unrelated variables, not two occurrences of one. The direct fix — save the `Var` into a
Kotlin `val` and reuse that reference — works, but it scales badly: real terms and clauses often involve
several variables reused across several sub-terms, and threading explicit Kotlin variables through nested
factory calls turns into exactly the kind of bookkeeping the rest of the API tries to avoid.

`Scope` exists to take over that bookkeeping. A `Scope` is a small, stateful factory of terms that caches
the variables it has created by their simple name: asking a `Scope` for `varOf("X")` a second time hands
back the *same* `Var` instance it produced the first time, instead of minting a new one. Everything else a
`Scope` offers — `structOf`, `listOf`, `ruleOf`, and so on — mirrors the static factories on `Term`'s
subtypes, so building a term inside a `Scope` reads the same as building one outside of it; the only
difference is that variable reuse becomes automatic instead of manual. Anonymous variables are the
deliberate exception: each call to `anonymous()` still produces a genuinely fresh variable, because
anonymous variables are never meant to be shared.

A `Scope` is intentionally mutable and single-use — it accumulates variable bindings as you use it and is
not meant to be reset or shared across unrelated terms. The natural granularity is one `Scope` per
independent unit of construction (typically one clause), which keeps the "same name means same variable"
rule scoped to exactly where a Prolog programmer would expect it: within one clause, not across the whole
program.

## Why terms need to be *refreshed*

The same distinctness rule that makes `Var.of("X") != Var.of("X")` is also what makes clause reuse during
resolution correct rather than merely convenient. A clause stored in a knowledge base gets used — and
potentially unified against — many times over the course of a computation. If each use shared the clause's
actual variable instances, one goal's bindings for `X` would leak into every other goal that happens to
reuse the same clause. Standard SLD resolution avoids this by *renaming apart*: each time a clause is
selected, its variables are replaced with fresh ones before unification is attempted.

`Term.freshCopy()` is that operation: it walks a term and replaces each variable it contains with a new one
sharing the same simple name but a distinct complete name — consistently, so that if a variable occurs more
than once in the original term, all of its occurrences are replaced by the *same* fresh variable in the
copy (rather than each occurrence getting its own, which would silently change the term's meaning). Ground
terms are unaffected, since there is nothing to refresh. Under the hood, `freshCopy()` is implemented in
terms of `Scope` for exactly this reason — a fresh, empty `Scope` guarantees that repeated occurrences of
the same variable are refreshed consistently, and a `freshCopy(scope)` overload lets several related terms
be refreshed against one shared `Scope` when they need to keep referring to the same fresh variables as each
other.
