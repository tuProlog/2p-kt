package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName

interface EndState : State {

    override val isEndState: Boolean
        get() = true

    @JsName("solution")
    val solution: Solution

    override fun asEndState(): EndState = this

    override fun next(): Iterable<State> = emptyList()
}
