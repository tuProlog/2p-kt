# Migrate to compounds, goals and references

2P-Kt's term hierarchy now has compounds whose functors are variables (patterns), goals in place of directives, and
references to host values in the core. This page lists what changes for code written against earlier versions, and
how to adapt it.

## Terms

| Before | Now | What to do |
|---|---|---|
| `Directive`, `Directive.of(...)`, `isDirective`, `asDirective()` | `Goal`, `Goal.of(...)`, `isGoal`, `asGoal()` | rename; `Theory.directives`, `DirectiveListener` and the DSL's `directive { }` keep their names, as they name the role of goals in theories |
| `Clause.head: Struct?`, `Rule.head: Struct` | `Compound?`, `Compound` | read `head.castToStruct()` where a name is needed, or handle patterns |
| APIs taking clause heads as `Struct` (`Fact.of`, `Rule.of`, `Theory.get(head)`, ...) | take `Compound` | none: a `Struct` is a `Compound` |
| `Constant.value: Any` | `Any?` | `null` only for `NullRef`: atoms and numbers keep non-null values |
| `TermVisitor`, `SyntaxNodeVisitor` | new `visitCompound`/`visitPattern`/`visitRef`/... methods | implementations of `SyntaxNodeVisitor` must implement `visitPattern` |
| `BindingStore` | new `isFunctorVariable`/`constrainFunctor` | custom binding stores must track which variables are functors of patterns |

Clause bodies are now normalized when clauses enter theories: variables in goal positions become `call/1` goals, so
`p(X) :- X` is stored as `p(X) :- call(X)`. Code inspecting stored clauses sees the normalized bodies.

## Solvers

| Before | Now | What to do |
|---|---|---|
| `Solver.solve(goal: Struct, options)`, abstract | `solve(goal: Goal, options)`, abstract; the other overloads take `Compound` | solver implementations override the `Goal` overload; callers keep passing `Struct`s |
| `Solution.query: Struct`, `Solution.Yes.solvedQuery: Struct` | `Goal` | read `query.body` / `solvedQuery.body` for the call; equality between solutions is unchanged |
| `Solution.yes/no/halt(query: Struct, ...)` | take `Compound` | none |
| `ConcurrentSolver.solveConcurrently(goal: Struct, ...)` | takes `Compound` | none |

Calls whose functor is an unbound variable, such as `F(X)`, unify with the heads of the clauses of their arity: see
[Metarules](../reference/solver-api.md#metarules).

## The OOP library

| Before | Now | What to do |
|---|---|---|
| `it.unibo.tuprolog.solve.libs.oop.Ref`, `ObjectRef`, `NullRef`: atoms named `<object:...>` | `it.unibo.tuprolog.core.Ref`, `ObjectRef`, `NullRef`: constants, not atoms | change imports; `ObjectRef.NULL` is `NullRef.instance`; `ObjectRef.of(nullable)` is `Ref.of(nullable)`; `.object` is `.value` |
| `TypeRef`, an interface | a class extending `ObjectRef`, wrapping a `KClass` | `TypeRef.of(type)` as before |
| `ref.invoke(...)`, `ref.assign(...)`, `typeRef.create(...)`: members | extension functions of `it.unibo.tuprolog.solve.libs.oop` | import them |
| `Result.asObjectRef(): ObjectRef?` | `asRef(): Ref?` | rename |
| `object_ref(null_reference)` succeeds | fails | test `ref/1` to accept the null reference too |

References are equal, and unify, only when they reference the same host object. Host objects are reached through
`HostInteropBackend`s: reflection on the JVM, the properties and functions of objects on JS, and any backend injected
with `OOPLib.withBackends(...)`.

## For 2ppy

`2ppy` exposes 2P-Kt to Python through JPype, and adapts its factories and wrappers. To migrate it:

- `tuprolog.core` should expose `Compound`, `Pattern`, `Goal`, `Ref`, `ObjectRef` and `NullRef`, with their factories:
  `Compound.of` (a `Struct` or a `Pattern`, depending on the functor), `Pattern.of`, `Goal.of`, `Ref.of` (nullable,
  giving a `NullRef` for `None`) and `ObjectRef.of` (non-null only). `Directive` adapters become `Goal` ones.
- `tuprolog.solve` adapters should accept any `Compound` as a query, and read the bodies of the query and of the
  solved query of solutions where they used them as calls.
- `tuprolog.libs` adapters of the OOP library should switch to the core references.
- Python objects can be reached from Prolog by registering a `HostInteropBackend`, through
  `OOPLib.withBackends(...)`. It holds opaque handles of Python objects in `ObjectRef`s, which compare handles by
  identity: one handle per Python object, kept alive by a registry on the Python side as long as Prolog terms
  reference it. The backend receives member names and positional arguments as terms (keyword arguments are not
  supported), returns `Result`s, and throws `OopException`s for missing members or unconvertible arguments. JPype
  cannot embed arbitrary Python objects into JVM values with their identity and callbacks, hence the handles.
