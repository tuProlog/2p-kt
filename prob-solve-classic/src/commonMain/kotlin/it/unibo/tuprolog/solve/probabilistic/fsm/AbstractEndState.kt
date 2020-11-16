package it.unibo.tuprolog.solve.probabilistic.fsm

import it.unibo.tuprolog.solve.probabilistic.ClassicProbabilisticExecutionContext
import it.unibo.tuprolog.solve.Solution

internal abstract class AbstractEndState(
    override val solution: Solution,
    override val context: ClassicProbabilisticExecutionContext
) : EndState, AbstractState(context) {

    override fun computeNext(): State = throw NoSuchElementException()
}
