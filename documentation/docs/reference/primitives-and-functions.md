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
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/primitive/Solve.kt:25:40"
```

- `signature` / `arguments` — what is being called and with what; validated at construction against arity/vararg;
- `context: C` — the `ExecutionContext` (an immutable snapshot of the solver's state — see
  [Solver API](solver-api.md#executioncontextaware)) current when the request was made;
- `startTime` / `maxDuration` (from `Durable`) — used to enforce timeouts;
- `query: Struct` — the request rebuilt as a `Struct`, lazily.

Because everything is `val`, a primitive cannot mutate its own request or the context it was given: `subSolver()`
spins up a fresh `Solver` sharing the request's context to recurse into sub-goals (used by e.g. `findall/3`), and any
change to global-looking state (the theory, flags, libraries, channels...) is expressed declaratively as data, not
performed in place — see below.

### Responses and side effects

A primitive replies by building a `Solve.Response` out of its `Request`, via one of the `replyWith*`/`replySuccess`/
`replyFail`/`replyException` helpers:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/primitive/Solve.kt:222:234"
```

- `solution: Solution` — the `Yes`/`No`/`Halt` produced for this response (see
  [Solver API](solver-api.md#solution));
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
