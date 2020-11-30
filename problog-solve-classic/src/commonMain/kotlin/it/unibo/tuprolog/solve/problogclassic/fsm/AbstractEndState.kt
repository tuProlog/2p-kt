package it.unibo.tuprolog.solve.problogclassic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext

internal abstract class AbstractEndState(
    override val solution: Solution,
    override val context: ProblogClassicExecutionContext
) : EndState, AbstractState(context) {

    override fun computeNext(): State = throw NoSuchElementException()
}
