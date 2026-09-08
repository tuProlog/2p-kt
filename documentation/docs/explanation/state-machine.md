# The State-Machine Solver (`:solve-classic`)

`:solve-classic` is 2P-Kt's ISO-standard, SLD-NF resolution engine, and the `Solver` you get from
`Solver.classic()` (see [Solver design](solver-design.md) for what a `Solver` is in general). What sets it apart
from other possible resolution strategies (e.g. `:solve-streams`) is *how* it resolves goals: not via the host
language's native call stack and recursion, but as an explicit, inspectable finite-state machine (FSM) that
steps through a fixed set of named locations. This design was formalised in a dedicated paper, "Formal Modelling
of a Prolog Solver as a State Machine" (Ciatto, 2021), which traces the approach back to Piancastelli's original
state-machine design for tuProlog. This page distills that formal model down to something a developer can use to
actually reason about the code, and cross-checks every claim against the current `solve-classic` sources.

The terminology has survived essentially unchanged from the 2021 paper to the current codebase: the paper's
nine "locations" map one-to-one onto nine `State` subtypes under
`it.unibo.tuprolog.solve.classic.fsm`:

| Paper location | Current class |
|---|---|
| Goal Selection | `StateGoalSelection` |
| Primitive Selection | `StatePrimitiveSelection` |
| Primitive Execution | `StatePrimitiveExecution` |
| Rule Selection | `StateRuleSelection` |
| Rule Execution | `StateRuleExecution` |
| Backtracking | `StateBacktracking` |
| Exception | `StateException` |
| End | `StateEnd` |
| Halt | `StateHalt` |

(There is also a `StateInit`, a bootstrap state producing the very first `StateGoalSelection`; it has no
counterpart in the paper because the paper's initial configuration is stipulated rather than computed.) "Primitive"
is likewise still the live term in the code for a built-in predicate implemented in Kotlin (as opposed to a
`Rule`/`Clause` resolved from a knowledge base) — see `Primitive` in `:solve` and `StatePrimitiveSelection`'s use
of `libraries.hasPrimitive(signature)`.

Every `State` shares the same tiny contract:

```kotlin
--8<-- "solve-classic/src/commonMain/kotlin/it/unibo/tuprolog/solve/classic/fsm/State.kt"
```

`next()` computes the successor state (a pure function of the current state's `context`), and a
`SolutionIterator` (with a `MutableSolutionIterator` variant supporting *hijacking* — overriding which state
comes next, e.g. for debugging or tracing) drives this `next()` loop and turns every visit to an end-state
(`isEndState`) into a `Solution` handed back to the caller. Running Prolog resolution as an explicit loop over
data, rather than as host-language recursion, means a resolution step is a value: it can be paused, resumed,
inspected, or hijacked without unwinding or rebuilding a JVM/JS call stack. It also sidesteps host stack-depth
limits for deeply recursive Prolog programs, since the "call stack" here is the execution-context stack
described below — ordinary heap data, not native stack frames.

## The two pieces of state every location reads and writes

Two data structures are threaded through every state transition:

- **The execution-context stack.** Each `ClassicExecutionContext` is one frame: a substitution, the stream of
  remaining goals, the stream of remaining candidate rules, the stream of remaining primitive responses, plus a
  `parent` link (so it *is* the stack, via chaining, rather than needing a separate stack container). Resolving
  a sub-goal pushes a new context whose parent is the current one; finishing a context's goals pops back to its
  parent (`StateGoalSelection`, when `context.goals.isOver`).
- **The choice-point queue**, modelled by the sealed `ChoicePointContext` (`Primitives` and `Rules` variants).
  Crucially, a choice point is *not* a single alternative — it is `pathToRoot`, effectively a full saved lineage
  of the execution-context stack at the moment the choice was recorded, which is exactly what makes it possible
  to resume an entirely different branch of the proof tree on backtracking:

```kotlin
--8<-- "solve-classic/src/commonMain/kotlin/it/unibo/tuprolog/solve/classic/ChoicePointContext.kt:8:13"
```

```kotlin
--8<-- "solve-classic/src/commonMain/kotlin/it/unibo/tuprolog/solve/classic/ChoicePointContext.kt:24:39"
```

Both **primitives** and **rules** are modelled uniformly as producers of *lazy streams* of alternatives
(`Cursor<out Solve.Response>` and `Cursor<out Rule>` respectively). This symmetry is the reason
`Primitive Selection`/`Rule Selection` and `Primitive Execution`/`Rule Execution` are structurally parallel
pairs of states: built-ins and user-defined clauses share the exact same choice-point/backtracking machinery,
and only one alternative is ever consumed per step — the rest stay in the cursor, to be pulled later on
backtracking. This is what realises Prolog's incremental, on-demand solution enumeration instead of eager
enumeration of every possible solution up front.

## Walking the nine states

1. **Goal Selection** — the entry point for every step. Three cases, visible directly in
   `StateGoalSelection.computeNext`: no goals left and no parent context → emit the current substitution as a
   solution and go to **End**; no goals left but a parent exists → pop the stack, carrying the child's
   substitution (filtered down to variables the parent still cares about) into the parent, and loop back into
   **Goal Selection** for the parent's remaining goals; goals remain → move to **Primitive Selection** (after
   applying `TrackVariables`-driven bookkeeping, if that flag is on).
2. **Primitive Selection** — looks up a `Primitive` for the goal's `(functor, arity)` signature in the current
   libraries (`libraries.hasPrimitive(signature)`). Found → build a child context, invoke the primitive
   (`primitive.solve(request)`), and move to **Primitive Execution** with the resulting response cursor already
   attached as a choice point. Not found → fall through to **Rule Selection**. A malformed goal (an unbound
   variable, or something that isn't callable) short-circuits straight to **Exception** here, before any lookup
   is attempted.
3. **Primitive Execution** — consumes exactly one response from the primitive's response stream. A substitution
   → merge it and return to **Goal Selection**. An empty stream → **Backtracking**. An exception in the response
   → **Exception**.
4. **Rule Selection** — the clause-resolution counterpart of Primitive Selection, and where several ISO
   special cases live directly in the code (`StateRuleSelection.computeNext`): `true`/`!` succeed without
   touching the knowledge base at all (`!` additionally triggers cut, below); `fail`/`false` and "goal doesn't
   exist anywhere" go to **Backtracking** or **Exception**, governed by the `Unknown` flag
   (`error`/`fail`/`warning`, see [Solver design](solver-design.md)); the general case queries the static KB,
   dynamic KB, and libraries' own theories (via `Theory.get`), `freshCopy()`s the matching clauses to rename
   variables apart, and moves to **Rule Execution** with the fresh rule stream as a new choice point.
5. **Rule Execution** — pops the first candidate rule, unifies its head with the goal. Success → the rule's body
   becomes the new goal stream, back to **Goal Selection**. Failure → **Backtracking**.
6. **Backtracking** — the central hub. Empty choice-point queue → **End**, emitting a negative solution
   (`Solution.no`). Otherwise it walks `pathToRoot` for the nearest choice point that `hasNext`, restores that
   saved execution-context lineage, and resumes at either **Primitive Execution** or **Rule Execution** depending
   on which kind of alternative was pending — a near-literal transcription of the paper's three backtracking
   rules:

   ```kotlin
   --8<-- "solve-classic/src/commonMain/kotlin/it/unibo/tuprolog/solve/classic/fsm/StateBacktracking.kt:9:25"
   ```
7. **Exception** — reached whenever a primitive's response, or an ISO error raised mid-resolution, carries an
   exception rather than a substitution. It climbs the execution-context stack (`context.parent`, one frame at a
   time, mirroring the paper's "search for a `catch/3` frame") looking for a currently-executing goal that is a
   `catch(Goal, Catcher, Recovery)` whose `Catcher` unifies with the exception. Found → the recovery goal becomes
   the new goal stream, resuming at **Goal Selection**. Reaching the root context without a match → **Halt**,
   with the exception attached to the emitted solution.
8. **End** — a *resumable* terminal: a solution (positive or negative) has just been emitted, but if the
   choice-point queue is non-empty, asking for the next solution loops back into **Backtracking** rather than
   truly stopping. This is what makes `solve()`'s lazy `Sequence<Solution>` work: pulling the next item from the
   sequence is what triggers this End → Backtracking transition.
9. **Halt** — the one true sink. Reached only via an uncaught exception; there is no transition back out of it,
   unlike End.

## Where the real code diverges from the formal model, deliberately

The formal paper describes cut abstractly as "prune the choice-point queue up to the parent goal's choice
point". The actual implementation (`StateRuleSelection.computeCutLimit`/`performCut`) is considerably more
careful, because ISO cut semantics are more subtle than that one-line description suggests:

- Cut must be **transparent** through conjunction (`,`), disjunction (`;`), and if-then (`->`) — i.e. a cut
  inside `(a , !, b)` cuts choice points belonging to the *clause* containing that conjunction, not to some
  imaginary choice point for the comma itself. `StateRuleSelection` keeps an explicit `transparentToCut` set of
  control-construct signatures for exactly this reason, walking up through them to find the real enclosing
  procedure.
- Cut also interacts with **last-call optimization** (`isTailRecursive`/`LastCallOptimization` flag): a tail
  call to the same predicate can reuse (`replaceWithChildAppendingRulesAndChoicePoints`) rather than grow
  (`createChildAppendingRulesAndChoicePoints`) the execution-context stack, which is an optimisation the formal
  model does not need to talk about at all since it treats the stack as unbounded.
- A separate `MagicCut` marker exists for cuts injected by other built-ins (e.g. `once/1`, if-then-else) that
  must cut back to the *caller's* choice point rather than the lexically enclosing clause — a wrinkle specific
  to how those built-ins are themselves implemented as ordinary rules over the same FSM (see
  `it.unibo.tuprolog.solve.classic.stdlib.rule`).

None of this changes the *shape* of the FSM — cut is still handled inside Rule Selection, still ends up at Goal
Selection, still manipulates the choice-point queue — but it is a good illustration of why "read the formal
model, then read the code" is the right way to use this page: the paper gives you the skeleton that is genuinely
still there in `solve-classic/src/commonMain/kotlin/it/unibo/tuprolog/solve/classic/fsm`, and the code fills in
the ISO-compliance details the abstract model intentionally leaves out.
