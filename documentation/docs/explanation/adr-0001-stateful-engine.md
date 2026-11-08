# ADR 0001: a stateful engine for `:solve-prolog`

- **Status:** accepted and implemented (issue #1010): the stateful machine is the only engine of `:solve-prolog`.
- **Date:** 2026-11-08.

## Context

Before #1010, `:solve-prolog` ran resolution as a finite-state machine over immutable values (see [the engine](state-machine.md)):
every transition copies a `PrologExecutionContext`, alternatives live in persistent `Cursor`s, and bindings are
composed `Substitution`s. This makes every step a snapshot, which was convenient for backtracking and debugging, but
costs an allocation pattern proportional to the work. Non-tail recursion, redo through deep stacks, and long list
processing are superlinear (100–240 s at depth 2000), and a primitive's next alternative is pulled before its
current answer is used.

#1010 replaces it with a stateful engine: mutable control storage, trailed bindings, lazily pulled alternatives, read-only
views at the extension boundary, debugger hooks, and pluggable selection. This record fixes the decisions and the
invariants the new engine must keep. The semantic contracts in `:test-solve` (listed in the
[solver test matrix](../reference/solver-test-matrix.md)) are their executable form.

## Decisions

### State ownership

Each resolution owns its machine; separate resolutions never share bindings, stacks, cursors or frames.

| State | Owner and rollback rule |
| --- | --- |
| Bindings, current goal and continuation, frame-local data | The active branch; restored on backtracking. |
| Clause environments and variable identity | Kept while reachable from a frame, choice point, view, solution or suspended branch. |
| Pending alternatives of a call | Owned by one continuation; pulled on demand, never cloned or replayed. |
| Knowledge bases, flags, libraries, operators, channels | The solver's world state; changed only through side effects, never rolled back by backtracking. |
| Custom data | Keeps its ephemeral, persistent and durable lifetimes. |
| Timing, cancellation, counters, invocation ids | The resolution; backtracking never rewinds them. |
| External I/O and foreign objects | Not reversible because engine state is. |

### Rollback invariants

- A stack mark is not a snapshot: frames needed by younger choice points are protected, and overwritten fields are
  saved, so backtracking into a callee that already returned works (`binding-restoration/redo-into-returned-callee`).
- A partial unification failure undoes all its bindings, even without a choice point.
- Cut removes the alternatives of its scope, recorded at predicate entry before the predicate's own alternatives; it
  never drops rollback records an older choice point still needs.

### View lifetime

Extensions (primitives, functions, interceptors, policies) see `ExecutionContext` as a read-only view, never the
machine. A request's or response's context reads the same values for as long as it is held, may compute them
lazily, and its knowledge bases change only through side effects. Terms that cross the boundary are public immutable
values: a public term never changes because an internal slot is reused. (The current engine's knowledge-base views
are still live; the new engine's generation-based store gives snapshots.)

### Source ownership and laziness

A call's alternatives come from a source it owns exclusively. The engine never pulls an alternative before it is
needed: no lookahead to decide whether to keep a choice point. A source reports *exhausted*, *deterministic* or
*unknown*; only the last one keeps a pending choice. `Primitive.solve(request): Sequence<Response>` stays supported
through an adapter that consumes the sequence once.

### Control scopes

Cut is transparent through `,`/2, `;`/2 and the branches of `->`/2, and opaque for the condition of `->`/2,
`call/N`, variable goals, `once/1`, `\+`, `catch/3` and `findall/3` (`controlScopeContract`). Scopes have logical
identities, so that a non-DFS scheduler can prune the branches of a scope rather than a stack index. `catch/3`
handlers form a chain, found without scanning ordinary frames.

### Transition interception

`TransitionInterceptor` (in `:solve`, passed via `SolveOptions.setInterceptor`) sees the ports of Byrd's box model,
`CALL`, `EXIT`, `REDO`, `FAIL` and `EXCEPTION`, for every call but control constructs. `before` runs before any
irreversible work of the transition and returns a `Decision`: `Continue`, `Fail` (at `CALL`, `EXIT` or `REDO`),
`Redirect` (at `CALL`) or `Halt`; a decision not allowed at a port is rejected. `after` runs once the transition has
happened. A `Transition` is valid only during its callback.

- No interceptor, no cost: the default path allocates nothing for interception.
- Last-call optimisation is off while an interceptor is set, as it would merge `EXIT`s.
- Deferred: `Pause`, until the async solve API exists (a synchronous debugger blocks inside `before`); `Restore`,
  retry and "select an alternative", until the machine has checkpoints that own resumable sources. History is never
  rebuilt by re-running primitives.
- `MutableSolutionIterator`, which replaced the next state wholesale, is removed.

### Selection policies

`RuleSelectionPolicy` orders (or filters, making search incomplete) a call's candidate rules; `GoalSelectionPolicy`
chooses the next goal of the current conjunction, where control constructs are single opaque goals. Both are in `:solve`,
passed via `SolveOptions`, and see read-only values. `null` means the default (source order, leftmost goal), which the
engine runs directly, with no policy object or view. Other orders are alternative semantics: they can change
termination, errors, cuts and side effects.

### Branch suspension

A FIFO over choice points that share one set of mutable frames is not breadth-first search. Non-DFS scheduling needs
resumable branches: the remaining goals, the binding state, the owned alternative sources, handlers and scopes, and the
logical search depth. A `BranchScheduler` interface comes with the suspension backend (copying at fork, paid only
when a scheduler keeps several branches), restricted to a pure fragment; its shape depends on that representation, so
it is not fixed here. Scheduling happens at inference boundaries, so a diverging branch doesn't starve the others.

### Compatibility

- Breaking changes are allowed in the `solve*` modules, and each public one is listed in its pull request:
  `Solve.Response`/`Compute.Response` carry a `context`; `MutableSolutionIterator` is gone.
- Custom unificators and custom theories keep working through compatibility paths; the fast paths are chosen by
  capability, never assumed.
- The old FSM stays as the reference engine until the new one passes every contract and the benchmarks, then it is
  removed.

## Consequences

- Extensions can no longer reach mutable engine state; anything they need must be offered through views.
- The contracts and the interception ports run against the current engine now, so the new engine is checked against
  the same expectations.
- Some #1010 features (pause, retry, non-DFS scheduling) arrive with the machine rather than as interfaces now.
