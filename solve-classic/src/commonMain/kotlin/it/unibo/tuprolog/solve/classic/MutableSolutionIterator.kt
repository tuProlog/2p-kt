package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.classic.impl.HijackableSolutionIterator
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A [SolutionIterator] that can *hijack* a state transition, i.e. substitute the [State] the machine actually
 * moves into for the one it would otherwise have computed.
 *
 * This exists for tooling built on top of `:solve-classic` -- step-by-step debuggers, tracers, or a REPL that
 * wants to pause resolution -- that need to redirect the FSM (e.g. forcing early termination, or replaying a
 * previously recorded state) without reimplementing resolution themselves.
 */
interface MutableSolutionIterator : SolutionIterator {
    /**
     * Called instead of [onStateTransition] before a transition from [source] to [destination] (the `index`-th
     * overall) is committed; returns the [State] that should actually become the new current state (typically
     * [destination] itself, unless the caller wants to hijack the transition).
     */
    @JsName("hijackStateTransition")
    fun hijackStateTransition(
        source: State,
        destination: State,
        index: Long,
    ): State

    companion object {
        /**
         * Creates a hijackable iterator starting from [initialState]. [hijackStateTransitionCallback] decides,
         * for each transition, which state is actually entered (by default, the computed destination, i.e. no
         * hijacking); [onStateTransitionCallback] is then notified of the (possibly hijacked) transition.
         */
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(
            initialState: State,
            hijackStateTransitionCallback: (State, State, Long) -> State = { _, dest, _ -> dest },
            onStateTransitionCallback: (State, State, Long) -> Unit = { _, _, _ -> },
        ): SolutionIterator =
            HijackableSolutionIterator(initialState, hijackStateTransitionCallback, onStateTransitionCallback)
    }
}
