package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.classic.impl.HijackableSolutionIterator
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface MutableSolutionIterator : SolutionIterator {
    @JsName("hijackStateTransition")
    fun hijackStateTransition(
        source: State,
        destination: State,
        index: Long,
    ): State

    companion object {
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
