# Errors and exceptions

2P-Kt maps the Prolog notion of exceptions onto Kotlin exception classes, aiming at a one-to-one correspondence with
the [ISO standard](http://www.gprolog.org/manual/html_node/gprolog020.html) errors while adding a handful of
implementation-specific ones (timeouts, halts, warnings).

## `TuPrologException`

The common root, in `it.unibo.tuprolog.core.exception` (module `:core`), extending Kotlin's `RuntimeException`:

```kotlin
--8<-- "core/src/commonMain/kotlin/it/unibo/tuprolog/core/exception/TuPrologException.kt:1:13"
```

Most subclasses across the codebase extend it directly for module-local concerns unrelated to resolution, e.g.
`SubstitutionException` (`:core`, thrown when a `Substitution.Fail` is coerced into a `Unifier`),
`NoUnifyException`/`OccurCheckException` (`:unify`, thrown by unification helpers that expect success), and
`LibraryException`/`NoSuchALibraryException`/`AlreadyLoadedLibraryException` (`:solve`, library loading errors).

## `ResolutionException`

Exceptions that occur *during* a `Solver`'s execution extend `ResolutionException` (`it.unibo.tuprolog.solve.exception`,
module `:solve`), which adds a stack of `ExecutionContext`s localizing where the exception occurred:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/exception/ResolutionException.kt:17:37"
```

- `contexts: Array<ExecutionContext>` — one entry per nested resolution frame; `context` is the first (innermost);
- `logicStackTrace: List<Struct>` — the exception's stack trace expressed as Prolog `Struct`s, derived from the
  contexts;
- `updateContext`/`updateLastContext`/`pushContext` — return a new exception with an updated/extended context stack
  (exceptions are immutable; subclasses override these to preserve their concrete type).

`ResolutionException` has three direct kinds of descendants:

### `LogicError` — ISO Prolog errors

`LogicError` (abstract) represents the standard `error(Type, Context)` term of ISO Prolog:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/exception/LogicError.kt:40:46"
```

- `type: Struct` — the error's type structure, e.g. `type_error(integer, foo)`;
- `extraData: Term?` — arbitrary implementation-defined payload;
- `errorStruct: Struct` — the full `error(type, extraData)` term, lazily built.

`LogicError.of(message, cause, context, type, extraData)` is a factory that inspects `type`'s functor/arity and
instantiates the matching subclass below, falling back to an anonymous `LogicError` if none matches:

| Subclass | Raised when |
|---|---|
| `DomainError` | a value has the right type but is outside the admissible domain (`expectedDomain: Expected`, `culprit: Term`) |
| `EvaluationError` | an arithmetic expression can't be evaluated (`errorType: Type`, e.g. `zero_divisor`, `int_overflow`) |
| `ExistenceError` | an object the operation depends on doesn't exist (`expectedObject: ObjectType`, `culprit: Term`) |
| `InstantiationError` | a `Term` is an unbound `Var` where it shouldn't be (`culprit: Var`) |
| `MessageError` | no other `LogicError` fits; free-form (`content: Term`) |
| `PermissionError` | a prohibited operation was attempted (`operation`, `permission`, `culprit: Term`) |
| `RepresentationError` | an implementation limit was hit (`limit: Limit`, e.g. `max_arity`) |
| `SyntaxError` | parsing failed |
| `SystemError` | an internal/uncaught problem occurred; left unhandled, it halts the engine |
| `TypeError` | a value doesn't have the expected type (`expectedType: Expected`, `culprit: Term`) |

Note: earlier revisions of this documentation named this base class `PrologError`; the class was subsequently
renamed to `LogicError` and the outdated `TuPrologRuntimeException` name (from even earlier revisions) no longer
exists in the codebase — `ResolutionException` is the actual base.

### `HaltException`

Signals that resolution must stop immediately (raised by `halt/0`/`halt/1`), carrying `exitStatus: Int` (default 0).

### `TimeOutException`

Raised when a `Solver` exceeds its allotted `maxDuration`; carries `exceededDuration: TimeDuration`.

### `Warning`

Abstract base for *non-fatal* conditions reported on the `warnings` output channel (see
[Solver API](solver-api.md#channels)) rather than aborting resolution. Concrete subtypes:

- `InitializationIssue(goal, cause)` — an initialization directive failed or errored;
- `MissingPredicate(signature)` — a call was made to an unknown predicate.

## Working with exceptions

A `Solver` never throws `ResolutionException`s directly out of `solve(...)`: a failed or halted goal instead produces
a `Solution.No` or `Solution.Halt` (the latter carrying the `ResolutionException`) — see
[Solver API](solver-api.md#solution). Exceptions are thrown, and expected to be caught, inside `Primitive`/
`LogicFunction` implementations; `Solve.Request.replyException(exception)` (or simply letting the exception
propagate, which the surrounding `PrimitiveWrapper` machinery turns into a `Solution.Halt`) is how a primitive
signals one. See [Primitives and functions](primitives-and-functions.md).
