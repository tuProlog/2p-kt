package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of selecting a rule to be solved to demonstrate a goal
 *
 * @author Enrico
 */
internal class StateRuleSelection(
        override val solveRequest: Solve.Request,
        override val executionScope: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionScope, solverStrategies) {

    override suspend fun behaveTimed(): Sequence<State> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
