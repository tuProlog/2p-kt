package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.classic.impl.SimpleSolutionIterator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Drives the `:solve-classic` finite-state machine, one [State.next] step at a time, and turns every visited
 * end-state (see [it.unibo.tuprolog.solve.classic.fsm.EndState]) into an emitted [Solution].
 *
 * This is the piece [AbstractClassicSolver.solveImpl] wraps into a lazy [Sequence]: [hasNext] answers whether
 * the current [state] is (or can, via backtracking, resume being) an end-state, and [next] steps the machine
 * forward until it reaches one, returning its [it.unibo.tuprolog.solve.classic.fsm.EndState.solution]. Because
 * this is a genuine [Iterator] over an explicit state value rather than host-language recursion, resolution can
 * be driven incrementally -- one solution, or even one FSM transition, at a time -- without unwinding a call
 * stack, and [onStateTransition] gives a hook to observe (e.g. log, trace, or count) every step as it happens.
 *
 * @see MutableSolutionIterator for a variant that can additionally *hijack* which state comes next.
 */
interface SolutionIterator : Iterator<Solution> {
    /** The state the machine is currently in (before the next call to [next] advances it). */
    @JsName("state")
    val state: State

    /** How many transitions the underlying state machine has performed so far. */
    @JsName("step")
    val step: Long

    override fun hasNext(): Boolean

    override fun next(): Solution

    /** Invoked after every transition from [source] to [destination] (the `index`-th transition overall). */
    @JsName("onStateTransition")
    fun onStateTransition(
        source: State,
        destination: State,
        index: Long,
    )

    companion object {
        /**
         * Creates a plain [SolutionIterator] that starts from [initialState] and reports every transition to
         * [onStateTransition] (a no-op by default).
         */
        @JsName("of")
        @JvmStatic
        fun of(
            initialState: State,
            onStateTransition: (State, State, Long) -> Unit = { _, _, _ -> },
        ): SolutionIterator = SimpleSolutionIterator(initialState, onStateTransition)
    }
}
