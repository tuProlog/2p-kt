package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName

interface EndState : State {

    @JsName("solution")
    val solution: Solution

    @JsName("hasOpenAlternatives")
    val hasOpenAlternatives: Boolean
        get() = solution.isYes && context.hasOpenAlternatives

    override val isEndState: Boolean
        get() = true

    override fun asEndState(): EndState = this
}
