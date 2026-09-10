# Libraries

A `Library` bundles operators, clauses, primitives and functions into a named, loadable unit a `Solver` can use.
Multiple libraries can be loaded at once, grouped into a `Runtime`.

## `Pluggable`

`Library` extends `Pluggable` (`it.unibo.tuprolog.solve.library`), which defines what a library actually contributes:

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/library/Pluggable.kt:12:29"
```

- `operators: OperatorSet` — operators to register with the solver;
- `clauses: List<Clause>` — facts/rules/directives to load into the theory (see
  [Default predicates](default-predicates.md#rules-commonrules) for an example);
- `primitives: Map<Signature, Primitive>` and `functions: Map<Signature, LogicFunction>` — see
  [Primitives and functions](primitives-and-functions.md).

`Pluggable` also provides default lookup helpers: `contains(signature)`, `contains(operator)`, `hasPrimitive`,
`hasFunction`, `hasRule`, `rulesSignatures` (derived from `clauses`), and `hasProtected` (signatures a library
reserves — `PrimitiveWrapper`'s permission checks consult this, see
[Errors and exceptions](errors-and-exceptions.md)).

## `Library`

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/library/Library.kt:24:33"
```

The only addition over `Pluggable` is `alias: String` — the name a solver uses to refer to the library, following
the pattern `\w+(\.\w+)*` (segments separated by `Library.ALIAS_SEPARATOR`, i.e. `"."`). Aliasing lets a solver
disambiguate predicates with clashing signatures across libraries by qualifying them, e.g. `prolog.lang:is/2`.

Instances are built through the companion factories, not by implementing the interface directly:

- `Library.of(alias, primitives, clauses, operators, functions)` — full form (all collections default to empty);
- `Library.of(primitives, clauses, operators, functions)` — same, aliased `"default"`;
- `Library.of(alias, library)` — re-aliases an existing `Library`.

`CommonBuiltins` (see [Default predicates](default-predicates.md)) is the running example: an `AbstractLibrary`
object aliased `"prolog.lang"`, whose `clauses`/`primitives`/`functions` delegate to `CommonRules`/`CommonPrimitives`/
`CommonFunctions` respectively. `AbstractLibrary` and `AbstractPluggable` supply sensible defaults (empty
collections, `equals`/`hashCode`/`toString` based on `alias` + contents) so a custom library only needs to override
what it actually provides. `LibraryImpl` is the concrete class backing `Library.of(...)`; `ExtensionLibrary` wraps
another `Library` while overriding only some of its members.

## `Runtime`

A `Runtime` is an immutable, alias-indexed group of `Library` instances — what a `Solver`'s `libraries` property
holds (see [Solver API](solver-api.md#executioncontextaware)):

```kotlin
--8<-- "solve/src/commonMain/kotlin/it/unibo/tuprolog/solve/library/Runtime.kt:10:43"
```

`Runtime` itself implements `Pluggable` (its `operators`/`clauses`/`primitives`/`functions` are the union of all
contained libraries') and `Map<String, Library>` (keyed by alias), plus:

- `aliases` / `libraries` — the set of alias strings / `Library` instances it contains;
- `plus(library)` / `plus(runtime)` — add one or more libraries;
- `minus(library)` / `minus(alias)` / `minus(aliases)` — remove libraries;
- `update(library)` — replace an already-loaded library (same alias) with a new version;
- `asTheory(unificator)` — flattens all libraries' clauses into a single `Theory`.

Build one with `Runtime.empty()`, `Runtime.of(vararg library)`, or `Runtime.of(iterable/sequence)`.

## Loading and unloading

Outside of a running resolution, `MutableSolver` (see [Solver API](solver-api.md#mutablesolver)) exposes
`loadLibrary(library)`, `unloadLibrary(library)` and `setRuntime(runtime)` directly.

From *within* a primitive, the same effect is achieved declaratively, via the `SideEffect.AlterRuntime` family
attached to a `Solve.Response` — `LoadLibrary`, `UnloadLibraries`, `UpdateLibrary`, `AddLibraries`, `ResetRuntime` —
since a primitive cannot mutate the `ExecutionContext` it was given (see
[Primitives and functions](primitives-and-functions.md#responses-and-side-effects)).

Library-related failures are reported as `LibraryException` (`it.unibo.tuprolog.solve.library.exception`, extending
`TuPrologException` directly rather than `ResolutionException`): `NoSuchALibraryException` and
`AlreadyLoadedLibraryException`.

See [Solver design](../explanation/solver-design.md) for why libraries — rather than a monolithic built-in set — are
the unit of extension for the resolution engine.
