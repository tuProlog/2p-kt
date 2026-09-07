# Solver Design: Why Solvers Look the Way They Do

See [Solver API](../reference/solver-api.md) for the full method-by-method listing of `Solver`, `Solution`, and
friends. This page is about the *shape* of that API: what a solver actually is under the hood, what state it
carries, and why that state is split the way it is. Understanding this is a prerequisite for understanding
[The state-machine solver](state-machine.md), which is one concrete way of driving this state to a solution.

## A solver is strategy-agnostic on purpose

The `:solve` module defines `Solver`, `Solution`, `ExecutionContext`, `Library`, `FlagStore`, and `Channel`
without committing to *how* resolution is actually carried out. That is a deliberate split: `:solve-classic`
implements ISO-standard SLD-NF resolution as an explicit finite-state machine (see
[state-machine.md](state-machine.md)); `:solve-streams` implements a different, more minimalistic
side-effect-free strategy over the same `Solver` contract. Both are interchangeable from client code's point of
view — `Solver.classic()` and `Solver.streams()` return the same `Solver` interface — because everything a
resolution strategy needs to *read and mutate* while solving a goal is factored out into `ExecutionContext`,
not hardwired into `Solver` itself.

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/ExecutionContext.kt:17:44"
```

An `ExecutionContext` is the solver's entire mutable state, reified as a value: the current substitution, the
call stack trace, custom data, and (via `createSolver`/`update`) the unificator, libraries, flags, both
knowledge bases, and channels. Every resolution strategy is free to extend this with whatever bookkeeping it
personally needs (`:solve-classic`'s `ClassicExecutionContext` adds the goal/rule/primitive cursors and the
choice-point stack described in [state-machine.md](state-machine.md)) — but any code written against the
*generic* `ExecutionContext` interface keeps working regardless of which concrete strategy produced it.

## The "mutable aspects" of a solver

Conceptually, every `Solver` carries five kinds of state that can meaningfully change *during* a resolution
(this is the same list the old wiki page called out as the "mutable aspects of solvers", and it is worth
preserving because it is the actual design rationale, not just an API inventory):

- a set of **libraries**
- a set of **operators**
- a **static** and a **dynamic** knowledge base
- a set of **flags**
- an **input** and an **output** channel

Calling these "mutable" is slightly counter-intuitive given that 2P-Kt's data structures are immutable
end-to-end (terms, substitutions, theories — see [Clause databases and indexing](clause-db-and-rete.md)). The
resolution is: each of these five things is represented by an *immutable* data structure, but the solver's
*current* one changes over the course of resolution by being replaced with a new instance (e.g. `assertz/1`
during resolution replaces the dynamic KB with a new `Theory`, it does not mutate the old one in place). This
keeps every intermediate state snapshot-able and inspectable — which matters a great deal to a state-machine
solver that needs to save and restore whole execution snapshots for backtracking — while still letting the
running system evolve. A `MutableSolver` (see the Reference page) additionally allows *external* code to swap
these assets in between resolutions, which is a distinct concern from the solver mutating its own state while
resolving a single query.

### Libraries: the unit of extensibility

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/library/Library.kt:24:34"
```

A `Library` bundles everything a piece of built-in (or user-supplied) functionality needs to plug into a
solver: operators, clauses (a `Theory` of "library predicates" written in Prolog itself), primitives (built-ins
implemented in Kotlin — see [state-machine.md](state-machine.md) for how these interact with resolution), and
functions (arithmetic-style term-reducing helpers). Bundling all four together, rather than exposing four
separate registration points on `Solver`, is what makes it possible to ship a self-contained feature
(`:io-lib`, `:oop-lib`) as a single pluggable unit, and what makes `AliasedLibrary` meaningful: since more than
one library can be loaded, aliasing is the mechanism that resolves clashing predicate indicators between
libraries without either library needing to know about the other.

### Flags: read-mostly, solver-scoped configuration

A Prolog `flag` is a named, valued switch — some standard (`double_quotes`, `unknown`), some
implementation-specific — read via a `FlagStore` (an immutable `String → Term` map) and typed via `NotableFlag`
subclasses (`Unknown`, `DoubleQuotes`, `LastCallOptimization`, `TrackVariables`, `MaxArity`, in
`it.unibo.tuprolog.solve.flags`). Keeping flags as data (a map) rather than as scattered solver fields means the
whole configurable surface of a solver is enumerable, snapshot-able as part of `ExecutionContext`, and equally
settable whether the strategy is `:solve-classic` or `:solve-streams`.

Two of these flags are worth calling out because they visibly shape the *state machine's* behaviour, not just
cosmetic solver output: `Unknown` controls what happens in `Rule Selection` when a goal's predicate does not
exist at all (fail silently, raise an `ExistenceError`, or just warn — see `StateRuleSelection.missingProcedure`
in the codebase), and `LastCallOptimization` lets the classic solver avoid growing the execution-context stack
on genuine tail calls.

### Channels: solver I/O without a fixed transport

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/channel/Channel.kt:9:24"
```

Prolog built-ins like `write/1`, `read/1`, or warning reporting need *some* way to talk to the outside world, but
2P-Kt runs on the JVM, on JS, and (via `stdin()`/`stdout()`/`stderr()`/`warning()` being `expect`/`actual`
declarations) potentially other Kotlin targets with entirely different I/O primitives. `Channel<T>` genericizes
over the payload type and stays deliberately minimal (listeners, open/close) so that a JVM `stdout()` can wrap a
`java.io.PrintStream` while a JS one wraps `console.log`, and a `Solver` built-in only ever depends on the
`Channel` abstraction. `InputStore`/`OutputStore` then let a solver hold *several* named channels at once
(useful for `assert`ing a Prolog `stream` term for `format/3`-style I/O redirection), rather than assuming a
single stdin/stdout pair.

## Why the split matters

None of these five aspects need to change *type* when the resolution strategy changes; they only need to change
*value* as resolution proceeds. That is precisely what lets `:solve-classic`'s finite-state-machine engine
(described in detail in [state-machine.md](state-machine.md)) and `:solve-streams`'s alternative strategy share
one `Solver`/`ExecutionContext`/`Library`/`Solution` vocabulary while disagreeing entirely on *how* a goal
becomes a stream of solutions.
