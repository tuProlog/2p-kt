package it.unibo.tuprolog.solve.streams.solver.fsm.impl

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.streams.solver.fsm.AbstractState
import it.unibo.tuprolog.solve.streams.solver.fsm.IntermediateState
import it.unibo.tuprolog.solve.streams.solver.fsm.State
import it.unibo.tuprolog.solve.streams.solver.fsm.TimedState

/**
 * Base class for all States that should have a timed behaviour
 *
 * @author Enrico
 */
internal abstract class AbstractTimedState(
    /** The [Solve.Request] that guides the State behaviour towards [Response]s */
    override val solve: Solve.Request<ExecutionContext>,
) : AbstractState(solve),
    IntermediateState,
    TimedState {
    /** Internal cached currentTime at first behave() call, enabling identical re-execution of that state */
    private val stateCurrentTime by lazy { currentTimeInstant() }

    override fun behave(): Sequence<State> =
        when {
            // optimized without check, when maxDuration is infinite
            solve.maxDuration == TimeDuration.MAX_VALUE -> behaveTimed()

            timeIsOver(stateCurrentTime - solve.startTime, solve.maxDuration) ->
                sequenceOf(statEndHaltTimeout())
            else -> behaveTimed()
        }

    /** Called only if executionTimeout has not been reached yet, and computation should go on */
    protected abstract fun behaveTimed(): Sequence<State>

    /** A function to check if currently the timeout has expired and return the halt state if yes,
     * the provided [toYieldState] otherwise*/
    protected fun IntermediateState.ifTimeIsNotOver(toYieldState: State): State =
        when {
            timeIsOver(currentTimeInstant() - solve.startTime, solve.maxDuration) ->
                statEndHaltTimeout()
            else -> toYieldState
        }

    override fun getCurrentTime(): TimeInstant = stateCurrentTime

    /** A function to check if time for execution has ended */
    private fun timeIsOver(
        currentDuration: TimeDuration,
        maxDuration: TimeDuration,
    ) = currentDuration >= maxDuration

    override fun toString(): String = "${this::class} with $solve"

    companion object {
        /** An utility function to create the end Halt state to be returned upon timeout expiry */
        private fun IntermediateState.statEndHaltTimeout(): State =
            stateEndHalt(
                TimeOutException(
                    "Given time for `${solve.query}` computation (${solve.maxDuration}) wasn't enough for completion",
                    context = solve.context,
                    exceededDuration = solve.maxDuration,
                ),
            )
    }
}
