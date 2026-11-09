# Solver API

The `:solve` module defines the platform-agnostic API for goal resolution: `Solver`, `Solution`, `SolveOptions`,
`Library`/`Runtime`, and I/O `Channel`s. It defines *no* resolution algorithm itself — that's the job of the
implementation modules `:solve-prolog`, `:solve-concurrent`, and `:solve-problog`, each providing a
`SolverFactory`. This page documents the common surface all of them implement.

## `Solver`

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/Solver.kt:22:66"
```

Three families of methods trigger resolution of a `Struct` goal, differing in how solutions are collected:

- `solve(goal, options): Sequence<Solution>` — lazily or eagerly yields solutions, depending on `options.isLazy`;
- `solveList(goal, options): List<Solution>` — always eager (careful with infinite solution sets);
- `solveOnce(goal, options): Solution` — eagerly returns the first solution only (`options.setLimit(1)`).

Overloads accepting a `timeout: kotlin.time.Duration` instead of `options` are shorthands built on `SolveOptions.of(...)`.

`Solver` also extends `ExecutionContextAware` (see below) and exposes `copy(...)` / `clone()` to derive a new solver
sharing/overriding specific pieces of state.

### Obtaining a `Solver`

`Solver`'s companion object exposes one `SolverFactory` per implementation:

- `Solver.prolog` — the Prolog, SLD-resolution-based solver (`:solve-prolog`);
- `Solver.problog` — a probabilistic-logic-programming solver (`:solve-problog`);
- `Solver.concurrent` — a solver that parallelizes resolution (`:solve-concurrent`).

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
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/SolveOptions.kt:10:33"
```

- `isLazy` / `isEager` — controls whether `solve()` streams solutions on demand or computes eagerly;
- `timeout: Duration` — max execution duration (`SolveOptions.MAX_TIMEOUT`, i.e. `Duration.INFINITE`, by default);
- `chronometerFactory: ChronometerFactory?` — how resolution time is measured (see [Resolution time](#resolution-time));
  `null`, the default, means `ChronometerFactory.default`;
- `limit: Int` — caps the number of returned solutions (`SolveOptions.ALL_SOLUTIONS`, i.e. `-1`, by default);
- `customOptions: Map<String, Any>` — implementation-specific extra knobs.

Instances are built via the companion's factories: `allLazily()`, `someLazily(limit)`, `allEagerly()`,
`someEagerly(limit)`, their `...WithTimeout(...)` variants, or the general
`of(lazy, timeout, limit, chronometerFactory, customOptions)`. `SolveOptions.DEFAULT` is `allLazily()`.

### Resolution time

Each resolution is timed by its own `Chronometer` (`it.unibo.tuprolog.utils.time`, in `:utils`), created when it
starts by `SolveOptions.createChronometer()`, i.e. by `chronometerFactory` (or `ChronometerFactory.default`) with
`timeout`. Options hold no timing state, so the same `SolveOptions` can be shared by any number of resolutions.

- **Semantics.** Expiration is strict: resolution is aborted once the elapsed time is *greater than* `timeout`, so a
  zero timeout only expires once some time elapses, and `Duration.INFINITE` never does. A timeout halts resolution
  with a `Solution.Halt` carrying a `TimeOutException`, wherever it is detected (a resolution step, `sleep/1`, a
  nested resolution such as `findall/3`'s), and `catch/3` can't intercept it.
- **Propagation.** The chronometer is resolution-level state shared by every `ExecutionContext` of the resolution
  (`Durable.chronometer`, hence `Solve.Request.chronometer`): backtracking neither restores elapsed time nor
  replenishes the budget. Nested resolutions (`Solve.Request.solve(goal, limit)`) are timed by a *child scope*, on the
  same timeline, whose deadline never exceeds the parent's.
- **Lazy solutions.** In `:solve-prolog`, each solution gets the full budget: asking for the next one restarts the
  chronometer, so time the caller spends between solutions is not counted. In `:solve-concurrent`, which computes
  solutions eagerly in the background, one budget covers the whole resolution.
- **Implementations** (`ChronometerFactory`'s companion):
  - `direct(timeline)` reads the clock at every resolution step: the reference implementation;
  - `adaptive(policy, timeline)` (the default, `ChronometerFactory.default`) reads it only every so many steps, planned from the estimated duration of a step
    (`AdaptiveSamplingPolicy`), and never without a timeout. Expiration may be detected slightly late, never early;
    mandatory readings happen after each primitive call and before publishing a solution;
  - `simulated(timeline, durationPerLeap)` runs on virtual time (`Timeline.manual()`): each resolution step advances
    it by `durationPerLeap`, and `sleep/1` advances it without waiting. Timeout behaviour becomes deterministic and
    independent of the host machine, e.g. for tests:

```kotlin
val timeline = Timeline.manual()
val options = SolveOptions.allLazilyWithTimeout(20.milliseconds)
    .setChronometerFactory(ChronometerFactory.simulated(timeline, durationPerLeap = 1.milliseconds))
solver.solve(Struct.parse("sleep(1000)"), options) // halts with a TimeOutException, without waiting 1 s
```

### Measuring a resolution

Chronometers enforce timeouts. To *measure* a resolution, wrap its solutions with `timed()`
(`it.unibo.tuprolog.solve.timing`): `TimedSolutions.timing` reports the time to the first answer, the active time
spent producing each answer, the total active time, and the elapsed time. Elapsed time also counts the pauses between
the consumer's calls. It doesn't change how timeouts behave. The time source is a parameter, so a `TestTimeSource`
makes it deterministic.

For counts that don't depend on the machine, pass `ResolutionCounters` with `SolveOptions.setCounters`. `:solve-prolog`
then counts predicate calls, alternatives tried and choice points. ISO control constructs (`,`, `;`, `->`, `call/N`,
`catch/3`, `throw/1`) are not counted, so the counts measure logical work and not an engine's own steps. Nested
resolutions (`findall/3`, ...) are not counted yet.

```kotlin
val counters = ResolutionCounters()
val solutions = solver.solve(goal, SolveOptions.allLazily().setCounters(counters)).timed()
solutions.forEach { /* ... */ }
println("${solutions.timing.firstAnswer}, ${solutions.timing.active}, ${counters.inferences} inferences")
```

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
