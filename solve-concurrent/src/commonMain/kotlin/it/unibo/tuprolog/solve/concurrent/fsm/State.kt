package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolver
import kotlin.js.JsName

/**
 * One location of the `:solve-concurrent` finite-state machine: an explicit, inspectable representation of
 * "where resolution currently is" along one branch of the search tree, carrying the [ConcurrentExecutionContext]
 * it operates on. Mirrors `:solve-classic`'s own `State` (see that module's "state-machine" explanation for the
 * formal model this is based on: `StateGoalSelection`, `StatePrimitiveSelection`, `StatePrimitiveExecution`,
 * `StateRuleSelection`, `StateRuleExecution`, `StateException`, `StateEnd` and `StateHalt`).
 *
 * The key difference from `:solve-classic` is [next]'s return type: there, a state has exactly one successor, and
 * backtracking across the *other* matching clauses/solutions is driven explicitly by a choice-point stack;
 * here, [next] returns __every__ alternative successor state at once (e.g. one [StateRuleExecution] per matching
 * clause, or one [StatePrimitiveExecution] per solution a backtracking primitive can produce). It is
 * `it.unibo.tuprolog.solve.concurrent.ConcurrentSolverImpl` that turns this into actual parallelism: it launches
 * one coroutine per element of [next]'s result, so every alternative is explored concurrently rather than
 * sequentially -- this is the whole of `:solve-concurrent`'s "concurrent" behaviour, applied recursively at every
 * choice point down the tree.
 *
 * @see ConcurrentSolver
 */
interface State {
    /** Whether this state is a terminal one for the current branch, i.e. an [EndState]. */
    @JsName("isEndState")
    val isEndState: Boolean
        get() = false

    /** This state as an [EndState], or `null` if [isEndState] is `false`. */
    @JsName("asEndState")
    fun asEndState(): EndState? = null

    /**
     * This state as an [EndState].
     * @throws ClassCastException if [isEndState] is `false`.
     */
    @JsName("castToEndState")
    fun castToEndState(): EndState =
        asEndState() ?: throw ClassCastException("Cannot cast $this to ${EndState::class.simpleName}")

    /**
     * Computes every state this branch may transition into from here, one per alternative (matching clause,
     * disjunct, primitive solution, ...) available at this point; empty only for [EndState]s. Each element is
     * meant to be explored independently (and, in `:solve-concurrent`'s solver, concurrently) of the others.
     */
    @JsName("next")
    fun next(): Iterable<State>

    /** The execution context this state operates on. */
    @JsName("context")
    val context: ConcurrentExecutionContext

    /** Returns a copy of this state, replacing [context] with the given one (defaulting to the current [context]). */
    @JsName("clone")
    fun clone(context: ConcurrentExecutionContext = this.context): State
}
