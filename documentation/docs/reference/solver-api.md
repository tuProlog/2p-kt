# Solver API

The `:solve` module defines the platform-agnostic API for goal resolution: `Solver`, `Solution`, `SolveOptions`,
`Library`/`Runtime`, and I/O `Channel`s. It defines *no* resolution algorithm itself — that's the job of the
implementation modules `:solve-prolog`, `:solve-concurrent`, and `:solve-problog`, each providing a
`SolverFactory`. This page documents the common surface all of them implement.

## `Solver`

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/Solver.kt:17:83"
```

Solvers solve `Goal`s, i.e. headless clauses `:- Body`: `solve(goal: Goal, options)` is the one method solvers
implement, and runs `goal.body`. Every other overload takes any `Compound` (a `Struct` such as `p(X)`, a `Pattern`, or
a `Goal`) and solves its goal: `:- p(X)` for `p(X)`, the goal itself for a `Goal`, which is never wrapped again.
Solutions refer to that `Goal` as their `query`.

Two families of methods trigger resolution:

- `solve(goal, options): SolutionStream` — yields solutions lazily or eagerly, depending on `options.isLazy`, as a
  [`SolutionStream`](#consuming-solutions-solutionstream);
- `solveOnce(goal, options): Solution` — returns the first solution only (`options.setLimit(1)`), and closes the
  stream behind it.

To collect every solution, call `solve(goal).toList()` (careful with infinite solution sets). `solveList(goal, ...)`
still exists, deprecated in favour of `solve(goal, ...).toList()`, which it calls.

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
solver.solve(Struct.of("append", listA, listB, result)).use { solutions ->
    for (solution in solutions) println(solution)
}
```

### Consuming solutions: `SolutionStream`

`solve` returns a `SolutionStream` (#1010): a lazy, single-pass `Sequence<Solution>` that is also `AutoCloseable`,
and that can be cancelled.

- **Single pass.** It can be iterated only once; a second iteration throws `IllegalStateException`. Keep the result
  of `toList()` if you need the solutions twice.
- **Lazy.** With the default options, each solution is computed when it is asked for. Sequence operations such as
  `map`, `filter` or `take` are lazy too, but they return a plain `Sequence`: close the stream itself, not what they
  return.
- **`toList()`** collects the remaining solutions and then closes the stream.
- **`close()`** stops the resolution without a further solution, and releases what it holds right away. Call it from
  the thread consuming the stream.
- **`cancel()`** asks the resolution to stop. It can be called from any thread, or from an interceptor or a primitive
  of the same resolution. The solution being computed, or the next one asked for, is a `Solution.Halt` with a
  `ResolutionCancelledException`; then the stream ends. How soon the resolution notices depends on the solver:
    - `:solve-prolog` checks at every inference step, so it also stops a never-ending search, and the nested
      resolutions started from it (`Request.subSolver()`) stop as well; `:solve-problog` inherits this;
    - solvers built on `AbstractSolver`, such as `:solve-concurrent`, check between solutions;
    - cancellation cannot interrupt a primitive that blocks.

#### Using it correctly on each platform

Close the stream whenever you may stop before the end. Each platform has an idiom that does it for you, also when an
exception is thrown:

**Kotlin**

```kotlin
solver.solve(goal).use { solutions ->
    for (solution in solutions) {
        if (solution.isYes) break // leaving the block closes the stream
    }
}

val first = solver.solve(goal).use { it.first() } // or solver.solveOnce(goal)
val all = solver.solve(goal).toList() // closes the stream once collected
```

**Java**

```java
try (SolutionStream solutions = solver.solve(goal)) {
    for (Solution solution : solutions.asIterable()) {
        if (solution.isYes()) break; // leaving the block closes the stream
    }
}

// or as a java.util.stream.Stream, which closes the SolutionStream when it is closed
try (Stream<Solution> solutions = solver.solve(goal).stream()) {
    solutions.filter(Solution::isYes).findFirst();
}

List<Solution> all = solver.solve(goal).toList(); // closes the stream once collected
```

A `SolutionStream` is not a `java.lang.Iterable` itself (a Kotlin type cannot be both a `Sequence` and an
`Iterable` without making every collection operation ambiguous in Kotlin), hence `asIterable()`. `stream()` is
available on the JVM only.

**JavaScript**

```js
for (const solution of solver.solve(goal)) {
  if (solution.isYes) break; // break, return or an exception close the stream
}
```

Reaching the end of the loop closes the stream too. Outside `for...of`, call `close()` (or `cancel()`) on the
stream yourself.

#### What happens if you don't close it

Nothing breaks immediately, which is why it is easy to miss. A stream that is abandoned before its end, e.g. after
`take(1)` or a `break` outside `use`, is simply left to the garbage collector:

- **Resources held by primitives leak.** A primitive whose pending answers come from a file, a database cursor, a
  socket or an external process releases them only when its stream is closed (see
  [releasing a primitive's resources](primitives-and-functions.md#alternatives-how-solvers-pull-a-primitives-answers)).
  The garbage collector reclaims the objects but never calls their `close()`, so file handles, cursors or
  connections stay open until the process exits.
- **The resolution's state is kept longer.** Choice points, pending branches and the trail are dropped only when the
  garbage collector gets to them, rather than right away.

What closing does *not* save is computation: with the default lazy options, a stream that is no longer read does no
work. Stopping a running or never-ending resolution is `cancel()`'s job. For plain Prolog with the built-in primitives,
forgetting to close is therefore harmless; with libraries over external resources, it is a leak.

## `SolveOptions`

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/SolveOptions.kt:11:34"
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
resolutions (`findall/3`, ...) count into the same instance, through `ExecutionContext.counters`.

```kotlin
val counters = ResolutionCounters()
val solutions = solver.solve(goal, SolveOptions.allLazily().setCounters(counters)).timed()
solutions.forEach { /* ... */ }
println("${solutions.timing.firstAnswer}, ${solutions.timing.active}, ${counters.inferences} inferences")
```

### Observing and steering a resolution

A `TransitionInterceptor` (`it.unibo.tuprolog.solve.intercept`), passed with `SolveOptions.setInterceptor`, sees the
ports of each call, as in Byrd's box model: `CALL`, `EXIT`, `REDO`, `FAIL` and `EXCEPTION`. Control constructs have
no ports of their own; the goals inside them do. `before(transition)` runs before the resolution goes past a port and
returns a `Decision`:

- `Continue`
- `Fail`: at `CALL`, `EXIT` or `REDO`
- `Redirect(goal)`: at `CALL`
- `Retry`: at `EXIT`, `REDO` or `EXCEPTION`: run the call again from its `CALL`, its bindings undone and its choice
  points dropped. The outside world is not restored, and primitives are called anew rather than replayed. At `FAIL`
  the call's state is gone, so it is rejected there. `:solve-prolog` supports it; other solvers may reject it.
- `Halt`

A decision not allowed at a port throws an `IllegalArgumentException`. `after(transition)` runs once the resolution has
gone past the port. A `Transition` carries the port, the goal with its bindings, an invocation id shared by the call's
ports, the call depth, and a read-only context. It is valid only during the callback: solvers may compute its goal and
context when first read, so copy what you need to keep. While an interceptor is set, `:solve-prolog` turns last-call
optimisation off, so that every call shows its `EXIT`.

To pause a resolution, a debugger blocks in `before`, e.g. running the resolution on a thread of its own: it resumes,
once, when `before` returns. Callbacks must not advance the resolution they are called from (the stateful engine throws
an `IllegalStateException` if they do).

`SolveOptions.setRuleSelection` and `setGoalSelection` replace source clause order and leftmost goal selection with a
`RuleSelectionPolicy` or a `GoalSelectionPolicy` (`it.unibo.tuprolog.solve.search`). Other orders are alternative
semantics: they can change termination, errors, cuts and side effects. See
[ADR 0001](../explanation/adr-0001-stateful-engine.md) for the design.

`SolveOptions.setMetaResolution` chooses the functors of calls whose functor is an unbound variable: see
[Metarules](#metarules).

`SolveOptions.setBranchScheduler` chooses which branch of the search advances next. A `BranchScheduler` picks among
the suspended branches (`Branch`: an id, a proof depth, and the goals left with the branch's own bindings).
`BranchScheduler.DEPTH_FIRST` is standard Prolog and the default; `BranchScheduler.BREADTH_FIRST` advances the oldest
branch. With any scheduler other than depth-first, `:solve-prolog` keeps every alternative as a
branch of its own, with a copy of the bindings, and asks the scheduler after each resolution step, so an infinite
branch can't starve the others (`p :- p. p.` answers `p` breadth-first). It supports a pure fragment only: user
predicates, conjunctions, `true`, `fail`, and built-ins that neither change the world nor run goals (unification,
comparison, arithmetic, type checks, `functor/3`, `arg/3`, `=../2`, `copy_term/2`, `between/3`, `succ/2`). Anything
else (cut, control constructs, meta-calls, database updates, I/O, all-solutions predicates) ends the resolution with a
`SystemError` naming it. An error also ends the whole search. It can't be combined with an interceptor.

```kotlin
val tracer = object : TransitionInterceptor {
    override fun before(transition: Transition): Decision {
        println("${transition.port} ${transition.goal}")
        return Decision.Continue
    }
}
solver.solve(goal, SolveOptions.allLazily().setInterceptor(tracer)).toList()
```

## `Solution`

Every solution is one of three sealed subtypes of `Solution`:

- `Solution.Yes` — successful; `substitution: Substitution.Unifier` and `solvedQuery: Goal` (the query with the
  substitution applied: read `solvedQuery.body` for the solved call) are non-nullable;
- `Solution.No` — the goal failed; `substitution` is `Substitution.Fail`, `solvedQuery` is `null`;
- `Solution.Halt` — resolution was aborted by an exception; carries `exception: ResolutionException`.

All three share `query: Goal` (the original goal; solutions built from a `Compound` refer to its goal), `isYes`/`isNo`/`isHalt`, `asYes()`/`asNo()`/`asHalt()` (nullable
casts), `whenIs(yes = ..., no = ..., halt = ...)` for exhaustive pattern matching, and `valueOf(variable)` to read a
binding directly off the solution. Construct them via `Solution.yes(...)`, `Solution.no(...)`, `Solution.halt(...)`.

See [Errors and exceptions](errors-and-exceptions.md) for `ResolutionException` and its hierarchy.

## Metarules

Goals whose functors are variables, i.e. [`Pattern`s](term-hierarchy.md#pattern) such as `F(X)`, and *metarules*,
i.e. rules whose heads are patterns, resolve natively, by unification, as any other goal:

```prolog
f(1).
g(2).
stored(likes, alice, pizza).
P(X, Y) :- stored(P, X, Y).
compose(grandparent, parent, parent).
P(X, Y) :- compose(P, Q, R), Q(X, Z), R(Z, Y).
```

- A goal `F(X)` unifies with the heads of the clauses of arity 1 of the knowledge bases, concrete or patterns, in the
  order of the theory, binding `F`: here `F = f, X = 1`, then `F = g, X = 2`. No option is needed. Library
  predicates, control constructs included, are no candidates.
- A concrete goal `likes(alice, X)` selects the clauses of `likes/2` and the metarules of arity 2, in the order of the
  theory, binding `P = likes`. The body runs with `P` bound, so a body goal `P(A, B)` would call `likes(A, B)`, and `F(alice, X)`
  enumerates the relations the metarule defines (`F = likes, X = pizza`).
- Functors bound by a body goal select the procedures of the later ones: `grandparent(alice, X)` binds `Q` and `R` to
  `parent` through `compose/3`, then calls `parent/2` twice.
- Metarules never extend library predicates, as no clause can, but they apply to every user predicate of their arity,
  including the ones their bodies call: a binary metarule guarded by a binary fact, e.g.
  `P(X, Y) :- view(P, E), E(X, Y)`, defines `view/2` too, and calling `view(edge, E)` calls the metarule again,
  forever. Guard metarules with predicates of another arity, as `stored/3` and `compose/3` above, or use limits and
  timeouts.
- Built-ins running or inspecting goals accept patterns: `findall/3`, `bagof/3`, `setof/3`, `\+/1`, `clause/2`,
  `retract/1`. A functor bound to anything but an atom makes the goal non-callable: `F = 1, F(X)` raises a type error.
- A `MetaResolutionPolicy` (`it.unibo.tuprolog.solve.search`), set via `SolveOptions.setMetaResolution`, replaces
  unification for unbound functors: it lists the functors to try, each bound in turn, on backtracking, through a call
  of `'$select_functor'(Functor, Arity)` that interceptors see. `MetaResolutionPolicy.ENUMERATE` tries the predicates
  of the call's arity defined in the knowledge bases; any lambda `(goal, context) -> Sequence<Atom>` is a policy too.

The concurrent solver resolves patterns and metarules the same way, finding solutions in no particular order, but
supports no policy.

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
