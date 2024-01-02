package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext

abstract class AbstractEndState(
    override val solution: Solution,
    override val context: ClassicExecutionContext,
) : EndState, AbstractState(context) {
    override fun computeNext(): State = throw NoSuchElementException()
}
