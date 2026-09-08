# Solver API

The `:solve` module defines the platform-agnostic API for goal resolution: `Solver`, `Solution`, `SolveOptions`,
`Library`/`Runtime`, and I/O `Channel`s. It defines *no* resolution algorithm itself — that's the job of the
implementation modules `:solve-classic`, `:solve-streams`, `:solve-concurrent` and `:solve-problog`, each providing a
`SolverFactory`. This page documents the common surface all of them implement.

## `Solver`

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/Solver.kt:21:65"
```

Three families of methods trigger resolution of a `Struct` goal, differing in how solutions are collected:

- `solve(goal, options): Sequence<Solution>` — lazily or eagerly yields solutions, depending on `options.isLazy`;
- `solveList(goal, options): List<Solution>` — always eager (careful with infinite solution sets);
- `solveOnce(goal, options): Solution` — eagerly returns the first solution only (`options.setLimit(1)`).

Overloads accepting a `timeout: TimeDuration` instead of `options` are shorthands built on `SolveOptions.of(...)`.

`Solver` also extends `ExecutionContextAware` (see below) and exposes `copy(...)` / `clone()` to derive a new solver
sharing/overriding specific pieces of state.

### Obtaining a `Solver`

`Solver`'s companion object exposes one `SolverFactory` per implementation:

- `Solver.prolog` — the classic, SLD-resolution-based solver (`:solve-classic`; `Solver.classic` is a deprecated
  alias for the same factory);
- `Solver.problog` — a probabilistic-logic-programming solver (`:solve-problog`);
- `Solver.concurrent` — a solver that parallelizes resolution (`:solve-concurrent`);
- `Solver.streams` — an experimental, more side-effect-free solver (`:solve-streams`); marked `@Deprecated` as not
  production-ready.

Each `SolverFactory` (`it.unibo.tuprolog.solve.SolverFactory`) provides `defaultRuntime`, `defaultUnificator`,
`defaultFlags`, `defaultStaticKb`/`defaultDynamicKb`, default I/O channels, and factory methods `solverOf(...)`,
`solverWithDefaultBuiltins(...)`, `mutableSolverOf(...)` and `mutableSolverWithDefaultBuiltins(...)` (the
`WithDefaultBuiltins` variants add the standard-library `Library` — see
[Default predicates](default-predicates.md) — on top of whatever `Runtime` you pass). `newBuilder()` returns a
`SolverBuilder` for a more fluent, stepwise construction.

### Solving a query

```kotlin
val solver = Solver.prolog.solverWithDefaultBuiltins()
val solutions = solver.solve(Struct.of("append", listA, listB, result))
```

## `SolveOptions`

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/SolveOptions.kt:7:26"
```

- `isLazy` / `isEager` — controls whether `solve()` streams solutions on demand or computes eagerly;
- `timeout: TimeDuration` — max execution duration (`SolveOptions.MAX_TIMEOUT` by default);
- `limit: Int` — caps the number of returned solutions (`SolveOptions.ALL_SOLUTIONS`, i.e. `-1`, by default);
- `customOptions: Map<String, Any>` — implementation-specific extra knobs.

Instances are built via the companion's factories: `allLazily()`, `someLazily(limit)`, `allEagerly()`,
`someEagerly(limit)`, their `...WithTimeout(...)` variants, or the general `of(lazy, timeout, limit, customOptions)`.
`SolveOptions.DEFAULT` is `allLazily()`.

## `Solution`

Every solution is one of three sealed subtypes of `Solution`:

- `Solution.Yes` — successful; `substitution: Substitution.Unifier`, `solvedQuery: Struct` are non-nullable;
- `Solution.No` — the goal failed; `substitution` is `Substitution.Fail`, `solvedQuery` is `null`;
- `Solution.Halt` — resolution was aborted by an exception; carries `exception: ResolutionException`.

All three share `query: Struct` (the original goal), `isYes`/`isNo`/`isHalt`, `asYes()`/`asNo()`/`asHalt()` (nullable
casts), `whenIs(yes = ..., no = ..., halt = ...)` for exhaustive pattern matching, and `valueOf(variable)` to read a
binding directly off the solution. Construct them via `Solution.yes(...)`, `Solution.no(...)`, `Solution.halt(...)`.

See [Errors and exceptions](errors-and-exceptions.md) for `ResolutionException` and its hierarchy.

## `ExecutionContextAware`

Both `Solver` and the internal `ExecutionContext` (visible to primitives while they run) expose the same set of
"mutable aspects" of a resolution process:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/ExecutionContextAware.kt:16:44"
```

- `libraries: Runtime` — the loaded libraries (see [Libraries](libraries.md));
- `flags: FlagStore` — an immutable map of Prolog flags (name → `Term`), including implementation-defined
  `NotableFlag`s (`name`, `defaultTerm`, `admissibleValues`);
- `staticKb` / `dynamicKb: Theory` — the two knowledge bases a solver draws clauses from; only `dynamicKb` can be
  altered by `assert`/`retract` during resolution;
- `operators: OperatorSet` — currently known Prolog operators;
- `inputChannels` / `outputChannels` — `InputStore`/`OutputStore` maps of named `Channel`s, with `standardInput`,
  `standardOutput`, `standardError` and `warnings` as convenience shortcuts to the well-known ones.

## `MutableSolver`

`MutableSolver` extends `Solver` with methods to alter its state *between* resolutions: `loadLibrary`/`unloadLibrary`,
`setRuntime`, `loadStaticKb`/`loadDynamicKb`/`appendStaticKb`/`appendDynamicKb`/`resetStaticKb`/`resetDynamicKb`,
`assertA`/`assertZ`, `retract`/`retractAll`, `setFlag`, and `setStandardInput`/`setStandardOutput`/`setStandardError`/
`setWarnings`. Obtain one via `mutableSolverOf(...)` / `mutableSolverWithDefaultBuiltins(...)` on any `SolverFactory`.

During resolution itself, state changes (asserting a clause, loading a library, altering a flag...) are instead
expressed as `SideEffect`s attached to a primitive's `Solve.Response` — see
[Primitives and functions](primitives-and-functions.md#responses-and-side-effects).

## Channels

I/O with a `Solver` is abstracted through `Channel<T>` (`it.unibo.tuprolog.solve.channel`), generic in the type of
element it carries:

- `InputChannel<T>` — `read()`, `peek()`, `available`, `isOver`; built via `InputChannel.of(generator)`,
  `InputChannel.of(string)`, or the default `InputChannel.stdIn()`;
- `OutputChannel<T>` — `write(value)`, `flush()`; built via `OutputChannel.of(consumer)`, or the defaults
  `OutputChannel.stdOut()`, `OutputChannel.stdErr()`, `OutputChannel.warn(): OutputChannel<Warning>`.

Both extend `Channel<T>`, which supports `addListener`/`removeListener`/`clearListeners` (registering `Listener<T?>`
callbacks invoked on every element transiting the channel) and `close()`/`isClosed`. A `Solver`'s channels are
collected into `InputStore`/`OutputStore` (`Map<String, Channel<*>>`-like containers), reachable via
`Solver.inputChannels` / `Solver.outputChannels`.

See [Solver design](../explanation/solver-design.md) for the rationale behind the immutable-context, side-effect-list
architecture.
