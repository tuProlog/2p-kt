package it.unibo.tuprolog.solve.problogimpl.fsm

import it.unibo.tuprolog.solve.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.Solution

internal abstract class AbstractEndState(
    override val solution: Solution,
    override val context: ProblogClassicExecutionContext
) : EndState, AbstractState(context) {

    override fun computeNext(): State = throw NoSuchElementException()
}
