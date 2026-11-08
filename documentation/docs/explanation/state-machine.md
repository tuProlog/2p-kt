# The `:solve-prolog` engine

`:solve-prolog` is 2P-Kt's ISO-standard, SLD-NF resolution engine, and the `Solver` you get from `Solver.prolog()`
(see [Solver design](solver-design.md) for what a `Solver` is in general). It resolves goals not through the host
language's call stack, but with an explicit machine whose state is ordinary heap data: deep recursion doesn't overflow
the JVM/JS stack, and every step can be observed or steered.

Until #1010 the machine was a finite-state machine over immutable values, following "Formal Modelling of a Prolog
Solver as a State Machine" (Ciatto, 2021): nine named locations (goal selection, primitive selection and execution,
rule selection and execution, backtracking, exception, end, halt), each step copying an execution context. It made
every step a snapshot, at a cost proportional to the work: non-tail recursion, redo through deep stacks and long list
processing were superlinear. #1010 replaced it with a *stateful* machine, `machine/PrologMachine`, keeping the same
semantics. [ADR 0001](adr-0001-stateful-engine.md) records the decisions; this page explains how the machine works.

## State

One `PrologMachine` runs one resolution, as an `Iterator<Solution>`. It owns:

- **The continuation**: the goals still to run, as an immutable linked list (`Cont`). Choice points share it, so
  backtracking restores it by keeping a pointer, not by copying.
- **Frames**: one per call (`Frame`), recording what cut, `catch/3`, the logic stack trace and interceptors need: the
  called goal, the parent frame, the choice-point stack height when the call started (cut's barrier), the trail mark,
  and what runs after the call.
- **The choice-point stack** (`Choice`): the alternatives left of a call (an iterator over clauses or primitive
  responses), with its frame, continuation and trail mark.
- **Bindings with a trail** (`machine/Bindings`): a mutable map from variables to terms, and a trail of the variables
  bound, so that backtracking undoes every binding made since a mark. Goals are not resolved at each call: a call to
  a user predicate is unified with clauses as written, right in these bindings, through
  `IncrementalUnificator.unifyInto` (see [Implement a custom `Unificator`](../how-to/implement-a-custom-unificator.md)).
  Primitives get their arguments resolved.
- **The world**: knowledge bases, flags, libraries, operators, channels and custom data, as a `PrologExecutionContext`
  that changes only through primitives' side effects. The machine reports each new world to its solver.

## Steps

The paper's locations survive as the machine's operations:

| Paper location | `PrologMachine` |
|---|---|
| Goal Selection | `select`: the next goal of the continuation (or the one a `GoalSelectionPolicy` picks); the end of the continuation is an answer |
| Primitive Selection / Execution | `callPrimitive`, `respond`: the primitive's responses, pulled as its `Primitive.alternatives` allows |
| Rule Selection / Execution | `callRules`, `execute`: the candidate clauses (from libraries, static and dynamic knowledge bases, through their indexes), renamed and unified one at a time as they are pulled |
| Backtracking | `backtrack`: undo the trail to the newest choice point's mark, and resume its next alternative |
| Exception | `raise`: the innermost `catch/3` frame whose catcher unifies with the error, or a halt |
| End / Halt | an answer, or a halt solution ending the resolution |

A step either picks a goal or backtracks; nothing is copied unless a choice point needs it.

## Details worth knowing

- **Cut** removes the choice points above the barrier of the frame it cuts. It is transparent through `,`/2, `;`/2 and
  `->`/2 (their frames are skipped), and `MagicCut`, used by built-ins such as `once/1` and if-then-else, cuts the
  caller's frame.
- **Last-call optimisation**: a self-recursive call ending its caller's body replaces the caller's frame (flag
  `LastCallOptimization`, and never while an interceptor is set, so that every call shows its `EXIT`).
- **Database updates** follow the logical update view: a call's candidate clauses are those of the dynamic store's
  generation when the call started, pulled lazily.
- **Laziness**: a primitive's next answer is pulled only when the search backtracks into it, unless the primitive is
  `AT_MOST_ONE` or `LOOKAHEAD_SAFE` (see [Primitives and functions](../reference/primitives-and-functions.md)).
- **Observations**: what primitives see is a read-only view (`CallContext`) whose substitution and stack trace are
  computed on first read. Views that may be read after the bindings change (errors, warnings, interceptor views) are
  computed when made.
- **Interception**: a `TransitionInterceptor` sees the Byrd ports of each call and may fail, redirect, retry or halt
  it (see [Solver API](../reference/solver-api.md#observing-and-steering-a-resolution)).
- **Non-depth-first search**: with a `BranchScheduler` other than depth-first, each choice point becomes a suspended
  branch with a copy of the bindings, and the scheduler picks the branch to advance after each step (a pure fragment
  of Prolog only).
