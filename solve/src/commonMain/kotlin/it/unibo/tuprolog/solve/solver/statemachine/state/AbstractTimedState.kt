package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import it.unibo.tuprolog.solve.solver.statemachine.TimeDuration
import it.unibo.tuprolog.solve.solver.statemachine.TimeInstant
import it.unibo.tuprolog.solve.solver.statemachine.currentTime
import kotlinx.coroutines.CoroutineScope

/**
 * Base class for all States that should have a timed behaviour
 *
 * @author Enrico
 */
internal abstract class AbstractTimedState(
        override val solveRequest: Solve.Request,
        override val executionScope: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractState(solveRequest, executionScope, solverStrategies), TimedState {

    override fun behave(): Sequence<State> = when {
        timeIsOver(solveRequest.context.computationStartTime - getCurrentTime(), solveRequest.executionTimeout) ->
            sequenceOf(StateEnd.Timeout(solveRequest, executionScope, solverStrategies))
        else -> behaveTimed()
    }

    /** Called only if executionTimeout has not been reached yet, and computation should go on */
    protected abstract fun behaveTimed(): Sequence<State>

    override fun getCurrentTime(): TimeInstant = currentTime()

    /** A function to check if time for execution has ended */
    private fun timeIsOver(actualDuration: TimeDuration, maxDuration: TimeDuration) =
            actualDuration >= maxDuration
}
