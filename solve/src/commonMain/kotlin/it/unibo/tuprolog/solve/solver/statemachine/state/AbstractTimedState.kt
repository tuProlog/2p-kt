package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext
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
        override val context: ExecutionContext,
        override val executionScope: CoroutineScope,
        override val solverStrategies: SolverStrategies,
        protected open val executionTimeout: TimeDuration
) : AbstractState(context, executionScope, solverStrategies), TimedState {

    override fun behave(): Sequence<State> = when {
        timeIsOver(context.computationStartTime - getCurrentTime(), executionTimeout) ->
            sequenceOf(StateEnd.Timeout(context, executionScope, solverStrategies))
        else -> behaveTimed()
    }

    /** Called only if executionTimeout has not been reached yet, and computation should go on */
    protected abstract fun behaveTimed(): Sequence<State>

    override fun getCurrentTime(): TimeInstant = currentTime()

    /** A function to check if time for execution has ended */
    private fun timeIsOver(actualDuration: TimeDuration, maxDuration: TimeDuration) =
            actualDuration >= maxDuration
}
