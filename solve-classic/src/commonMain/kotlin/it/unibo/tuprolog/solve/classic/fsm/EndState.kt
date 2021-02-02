package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName

interface EndState : State {

    @JsName("solution")
    val solution: Solution

    @JsName("hasOpenAlternatives")
    val hasOpenAlternatives: Boolean
        get() = solution is Solution.Yes && context.hasOpenAlternatives
}
