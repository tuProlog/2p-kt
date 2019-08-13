package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext
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
        protected open val executionTimeout: TimeDuration
) : AbstractState(context, executionScope), TimedState {

    override fun behave(): State = when {
        timeIsOver(context.computationStartTime - getCurrentTime(), executionTimeout) ->
            StateEnd.Timeout(context)
        else -> behaveTimed()
    }

    /** Called only if executionTimeout has not been reached yet, and computation should go on */
    protected abstract fun behaveTimed(): State

    override fun getCurrentTime(): TimeInstant = currentTime()

    /** A function to check if time for execution has ended */
    private fun timeIsOver(actualDuration: TimeDuration, maxDuration: TimeDuration) =
            actualDuration >= maxDuration
}
