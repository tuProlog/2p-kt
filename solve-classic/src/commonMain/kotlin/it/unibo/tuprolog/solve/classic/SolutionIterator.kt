package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.classic.impl.SimpleSolutionIterator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface SolutionIterator : Iterator<Solution> {
    @JsName("state")
    val state: State

    @JsName("step")
    val step: Long

    override fun hasNext(): Boolean

    override fun next(): Solution

    @JsName("onStateTransition")
    fun onStateTransition(source: State, destination: State, index: Long)

    companion object {
        @JsName("of")
        @JvmStatic
        fun of(
            initialState: State,
            onStateTransition: (State, State, Long) -> Unit = { _, _, _ -> }
        ): SolutionIterator = SimpleSolutionIterator(initialState, onStateTransition)
    }
}
