# Primitives and functions

2P-Kt implements built-in predicates and arithmetic operators in Kotlin, rather than as Prolog clauses, through two
`fun interface`s in the `:solve` module: `Primitive` (predicates, called during resolution) and `LogicFunction`
(arithmetic functions, called while evaluating `is/2` and friends). Both are indexed by `Signature` inside a
`Library` — see [Libraries](libraries.md).

## Primitives

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/primitive/Primitive.kt:9:11"
```

A `Primitive` takes a `Solve.Request` and returns a `Sequence<Solve.Response>` — one response per solution the
primitive contributes (a deterministic predicate returns a one-element sequence, `between/3` returns as many
responses as integers in the range, `repeat/0` returns an infinite sequence, and so on).

### Requests and immutability

`Solve.Request` is an immutable `data class`:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/primitive/Solve.kt:25:36"
```

- `signature` / `arguments` — what is being called and with what; validated at construction against arity/vararg;
- `context: C` — the `ExecutionContext` (a read-only view of the solver's state — see
  [Solver API](solver-api.md#executioncontextaware)) current when the request was made. Its knowledge bases can't be
  changed in place: writing to them returns a copy;
- `chronometer` (from `Durable`, i.e. the context's) — the resolution's timing: `maxDuration`, `elapsedTime` and
  `remainingTime` derive from it, and `solve(goal, limit)` times nested resolutions by a child scope of it (see
  [Resolution time](solver-api.md#resolution-time));
- `query: Struct` — the request rebuilt as a `Struct`, lazily.

Because everything is `val`, a primitive cannot mutate its own request or the context it was given: `subSolver()`
spins up a fresh `Solver` sharing the request's context to recurse into sub-goals (used by e.g. `findall/3`), and any
change to global-looking state (the theory, flags, libraries, channels...) is expressed declaratively as data, not
performed in place — see below.

### Responses and side effects

A primitive replies by building a `Solve.Response` out of its `Request`, via one of the `replyWith*`/`replySuccess`/
`replyFail`/`replyException` helpers:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/primitive/Solve.kt:235:252"
```

- `solution: Solution` — the `Yes`/`No`/`Halt` produced for this response (see
  [Solver API](solver-api.md#solution));
- `context: ExecutionContext` — the context the response was produced in, i.e. its request's (every `reply*` helper
  sets it);
- `sideEffectManager: SideEffectManager?` — an optional low-level hook into the engine's control flow (used
  internally, e.g. by cut);
- `sideEffects: List<SideEffect>` — a description of state changes the engine should apply to the *next* context
  after this response, since the primitive itself cannot mutate its (immutable) `ExecutionContext`.

`SideEffect` (`it.unibo.tuprolog.solve.sideffects`) is a sealed hierarchy of such descriptions, grouped by what they
touch: knowledge bases (`AddStaticClauses`, `RemoveDynamicClauses`, `ResetDynamicKb`, ...), flags (`SetFlags`,
`ResetFlags`, `ClearFlags`), the library runtime (`LoadLibrary`, `UnloadLibraries`, `AddLibraries`, `ResetRuntime`),
operators (`SetOperators`, `RemoveOperators`), channels (`OpenInputChannels`, `CloseOutputChannels`, ...), and
custom per-request data at three lifetimes (`SetEphemeralData`, `SetPersistentData`, `SetDurableData` — backing the
`get_ephemeral/2`/`get_persistent/2`/`get_durable/2` primitives). Each knows how to `applyTo(context)` to produce the
next `ExecutionContext`.

### Alternatives: how solvers pull a primitive's answers

A primitive returns its answers as a `Sequence`, and a solver pulls the next one only when the search backtracks into
the primitive. To know whether a primitive has more answers, e.g. to avoid keeping a choice point after its last one,
the solver would have to pull ahead, which runs the primitive's code for an answer nobody asked for yet: a generator
would run one step early, and an alternative that throws or diverges would run even if a cut discards it.
`Primitive.alternatives` (`Alternatives`, `it.unibo.tuprolog.solve.primitive`) tells solvers what they may do:

- `AT_MOST_ONE`: at most one answer, so no choice point is needed;
- `LOOKAHEAD_SAFE`: more answers may follow, but computing the next one early is cheap, terminates and has no effects
  (side effects described by a `Solve.Response` only happen when the solver uses it), so solvers may look ahead;
- `ON_DEMAND` (the default): each answer is computed only when the search backtracks into the primitive. When a query's
  last answer comes from such a primitive, finding out there are no more takes one more step, so the query ends with a
  further `no`.

`PrimitiveWrapper` subclasses override `alternatives`. The `NonBacktrackable` base classes (`Predicative`, `Functional`,
type checks, ...) report `AT_MOST_ONE`, and the `WithoutSideEffects` ones `LOOKAHEAD_SAFE`. A primitive built from a
function declares it with `Primitive.of(alternatives) { request -> ... }`.

```kotlin
// yields 1, 2, 3: cheap and pure, so solvers may look ahead, and `gen(X)` ends after X = 3 without a further `no`
val gen = Primitive.of(Alternatives.LOOKAHEAD_SAFE) { request ->
    (1..3).asSequence().map { request.replyWith(request.unificator.mgu(request.arguments[0], Integer.of(it))) }
}
```

`:solve-prolog` follows `alternatives` (#1010).

An `ON_DEMAND` primitive's answers may hold a resource, e.g. a file read one line per answer. If the iterator of the
`Sequence` it returns implements `AutoCloseable`, `:solve-prolog` closes it once it needs no more answers from it:
when they run out (or, for `AT_MOST_ONE`, after the first), or earlier on a cut, an exception unwinding past its choice
point, a cancellation, or `SolutionStream.close()` (#1010). An abandoned plain `solve` sequence closes nothing. Only the primitive's own iterator is closed: the relation base classes (`BinaryRelation`, ...) map
their answers into a new sequence, so a primitive that needs this returns its responses directly.

### `PrimitiveWrapper`

Concrete primitives extend `PrimitiveWrapper<C>`, implementing `uncheckedImplementation(request): Sequence<Solve.Response>`;
the base class wraps it with `Primitive.enforcingSignature` so a mismatched signature throws instead of running:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/stdlib/primitive/Between.kt:13:20"
```

`PrimitiveWrapper`'s companion also carries a library of `Solve.Request` extension functions used for argument
validation, each throwing the appropriate `LogicError` (see [Errors and exceptions](errors-and-exceptions.md)) on
failure and returning `this` otherwise, so they chain fluently:
`ensuringArgumentIsInstantiated`, `ensuringArgumentIsInteger`, `ensuringArgumentIsAtom`, `ensuringArgumentIsList`,
`ensuringArgumentIsCallable`, `ensuringArgumentIsWellFormedIndicator`, `ensuringProcedureHasPermission`, and more.

## Functions

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/function/LogicFunction.kt:13:15"
```

A `LogicFunction` takes a `Compute.Request` and returns a single `Compute.Response` — functions are pure and
deterministic (no side effects, no multiple solutions), matching how Prolog arithmetic expressions behave:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/function/Compute.kt:14:31"
```

`Compute.Request` mirrors `Solve.Request` (`signature`, `arguments`, `context`, timing) minus anything related to
non-determinism or side effects; `replyWith(result: Term)` builds the `Response`.

Concrete functions extend `FunctionWrapper<C>` (implementing `uncheckedImplementation`), most commonly through the
narrower `MathFunction` base, which adds helpers to raise the right `EvaluationError`/`TypeError`
(`throwIntOverflowError`, `throwZeroDivisorError`, `throwUndefinedError`, ...). Arity-specific subclasses
(`NullaryMathFunction`, `UnaryMathFunction`, `BinaryMathFunction`, `IntegersBinaryMathFunction`) let implementers
overload per numeric-type combination instead of pattern-matching manually:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/stdlib/function/Addition.kt:15:29"
```

See [Default predicates](default-predicates.md) for the full catalogue of built-in primitives, functions and rules,
and [Solver design](../explanation/solver-design.md) for why side effects are represented as data instead of being
applied directly.
