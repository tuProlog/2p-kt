package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution

internal abstract class AbstractEndState(
    override val solution: Solution,
    override val context: ExecutionContextImpl
) : EndState, AbstractState(context) {

    override fun computeNext(): State = throw NoSuchElementException()
}