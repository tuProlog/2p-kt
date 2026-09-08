package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import kotlin.js.JsName

/**
 * A [State] that carries an emitted [Solution] (`StateEnd`, reached on a positive or negative solution, or
 * `StateHalt`, reached on an uncaught exception). Every time [it.unibo.tuprolog.solve.classic.SolutionIterator]
 * visits one of these, [solution] is what it hands back to the caller.
 */
interface EndState : State {
    /** The solution emitted by reaching this state. */
    @JsName("solution")
    val solution: Solution

    /** Whether asking for another solution after this one would resume resolution (via backtracking) rather than truly stop. */
    @JsName("hasOpenAlternatives")
    val hasOpenAlternatives: Boolean
        get() = solution.isYes && context.hasOpenAlternatives

    override val isEndState: Boolean
        get() = true

    override fun asEndState(): EndState = this
}
