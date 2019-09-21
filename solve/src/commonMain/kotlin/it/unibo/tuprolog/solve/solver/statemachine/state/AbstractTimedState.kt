package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TimeOutException
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
        override val executionStrategy: CoroutineScope
) : AbstractState(solveRequest, executionStrategy), TimedState {

    /** Internal cached currentTime at first behave() call, enabling identical re-execution of that state */
    private val stateCurrentTime by lazy { getCurrentTime() }

    override fun behave(): Sequence<State> = when {
        // optimization could be made (calling directly behaveTimed()) when solveRequest.executionTimeout is Long.MAX_VALUE
        // avoiding timeIsOver check
        timeIsOver(stateCurrentTime - solveRequest.context.computationStartTime, solveRequest.executionTimeout) ->
            sequenceOf(
                    StateEnd.Halt(solveRequest, executionStrategy, TimeOutException(
                            "Given time for `${solveRequest.query}` computation (${solveRequest.executionTimeout}) wasn't enough for completion",
                            context = solveRequest.context,
                            deltaTime = stateCurrentTime - solveRequest.context.computationStartTime
                    ))
            )
        else -> behaveTimed()
    }

    /** Called only if executionTimeout has not been reached yet, and computation should go on */
    protected abstract fun behaveTimed(): Sequence<State>

    override fun getCurrentTime(): TimeInstant = currentTime()

    /** A function to check if time for execution has ended */
    private fun timeIsOver(currentDuration: TimeDuration, maxDuration: TimeDuration) =
            currentDuration >= maxDuration
}
