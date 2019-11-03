package it.unibo.tuprolog.solve.solver.fsm.state.impl

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.Solve.Response
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.solver.fsm.state.AbstractState
import it.unibo.tuprolog.solve.solver.fsm.state.IntermediateState
import it.unibo.tuprolog.solve.solver.fsm.state.State
import it.unibo.tuprolog.solve.solver.fsm.state.TimedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Base class for all States that should have a timed behaviour
 *
 * @author Enrico
 */
internal abstract class AbstractTimedState(
        /** The [Solve.Request] that guides the State behaviour towards [Response]s */
        override val solve: Solve.Request<ExecutionContext>,
        override val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : AbstractState(solve, executionStrategy), IntermediateState, TimedState {

    /** Internal cached currentTime at first behave() call, enabling identical re-execution of that state */
    private val stateCurrentTime by lazy { getCurrentTime() }

    override fun behave(): Sequence<State> = when {
        solve.executionMaxDuration == TimeDuration.MAX_VALUE -> behaveTimed() // optimized without check, when maxDuration is infinite

        timeIsOver(stateCurrentTime - solve.requestIssuingInstant, solve.executionMaxDuration) ->
            sequenceOf(stateEndHalt(
                    TimeOutException(
                            "Given time for `${solve.query}` computation (${solve.executionMaxDuration}) wasn't enough for completion",
                            context = solve.context,
                            exceededDuration = solve.executionMaxDuration
                    )
            ))
        else -> behaveTimed()
    }

    /** Called only if executionTimeout has not been reached yet, and computation should go on */
    protected abstract fun behaveTimed(): Sequence<State>

    override fun getCurrentTime(): TimeInstant = currentTimeInstant()

    /** A function to check if time for execution has ended */
    private fun timeIsOver(currentDuration: TimeDuration, maxDuration: TimeDuration) =
            currentDuration >= maxDuration
}
