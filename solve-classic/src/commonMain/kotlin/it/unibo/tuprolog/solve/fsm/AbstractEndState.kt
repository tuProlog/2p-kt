package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext
import it.unibo.tuprolog.solve.Solution

internal abstract class AbstractEndState(
    override val solution: Solution,
    override val context: ClassicExecutionContext
) : EndState, AbstractState(context) {

    override fun computeNext(): State = throw NoSuchElementException()
}
