# Solver test matrix

Which shared conformance contracts of `:test-solve` each solver runs, and why some cases are left out. A contract is a
Kotest factory (`xContract(solverFactory, disabled)`). Each solver's spec `include`s it and lists in `disabled` the case
ids it does not support. This page records the reasons for those lists.

| Solver | Spec | Shared contracts | Disabled cases |
|---|---|---|---|
| `:solve-prolog` | `PrologSolverSpec` | all | none |
| `:solve-problog` | `ProblogSolverSpec` | ISO built-ins and solver contracts; not yet the #1010 semantic contracts | ~36 cases: ProbLog rewrites user clauses, so goals built at run time cannot reach user predicates |
| `:solve-concurrent` | `ConcurrentSolverSpec` | construction, static factory, mutable solver | behaviour is tested by its own JVM-only suite. It will get its own async suite (coroutine-based solving) rather than the sequential contracts |
| `:solve-plp` | `ProbabilitySpec` | none | probabilistic semantics, not a Prolog solver |

## Semantic contracts (#1010, step 0)

These pin down behaviour that a stateful, trail-based engine could break without any ISO conformance test noticing:

| Contract | What it checks |
|---|---|
| `bindingRestorationContract` | bindings undone after partial unification failure and through aliases; redo into a callee that already returned |
| `controlScopeContract` | which constructs a cut sees through: if-then condition, `call/1`, `\+`, `once/1`, `catch/3`, `findall/3` are opaque; branches, `,` and `;` are transparent |
| `catchRestorationContract` | `catch/3` undoes the goal's bindings and alternatives before recovery; reactivation on redo; rethrow |
| `logicalUpdateViewContract` | a running call sees the clauses that existed when it started (ISO 7.5.4) |
| `lazinessContract` | alternatives are pulled on demand: `take(1)`, cut and `once/1` don't pull more, repeated `hasNext()` doesn't advance |
| `lifetimeContract` | solutions, primitive requests and errors read the same after the solver moves on |
| `interceptionContract` | the ports a `TransitionInterceptor` sees (call, exit, redo, fail, exception), and its decisions |
| `selectionPolicyContract` | reversed clause order, and a non-leftmost goal selection that keeps the answers |

`:solve-prolog`'s `DeepExecutionSpec` also runs, through the JVM task `jvmTestSmallStack`, with a 1 MB stack instead
of the 256 MB used by `jvmTest`, so that accidental recursion in the engine shows up. It is part of `check`.

### Notes on `:solve-prolog`

- **On-demand primitives.** The engine pulls a primitive's next answer only when the search backtracks into it, unless
  the primitive declares otherwise (`Primitive.alternatives`, see [Primitives and functions](primitives-and-functions.md)).
  A query whose last answer comes from an on-demand primitive therefore ends with a further `no`.
- **Deep execution.** `DeepExecutionSpec` runs tail and non-tail recursion, redo through a deep stack and long list
  processing at depth 20,000 (the FSM engine before #1010 was superlinear on the last three, 100–240 s at depth 2000).
