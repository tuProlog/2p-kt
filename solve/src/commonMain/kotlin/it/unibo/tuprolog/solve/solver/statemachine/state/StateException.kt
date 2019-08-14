package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * State that's responsible of managing exceptions that can be thrown during execution
 *
 * @author Enrico
 */
internal class StateException(
        override val solveRequest: Solve.Request,
        override val executionScope: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionScope, solverStrategies) {

    override fun behaveTimed(): Sequence<State> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
