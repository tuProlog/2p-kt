package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.Solve.Response
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.solver.fsm.AbstractState
import it.unibo.tuprolog.solve.solver.fsm.IntermediateState
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.fsm.TimedState

/**
 * Base class for all States that should have a timed behaviour
 *
 * @author Enrico
 */
internal abstract class AbstractTimedState(
    /** The [Solve.Request] that guides the State behaviour towards [Response]s */
    override val solve: Solve.Request<ExecutionContext>
) : AbstractState(solve), IntermediateState, TimedState {

    /** Internal cached currentTime at first behave() call, enabling identical re-execution of that state */
    private val stateCurrentTime by lazy { currentTimeInstant() }

    override fun behave(): Sequence<State> = when {
        solve.executionMaxDuration == TimeDuration.MAX_VALUE -> behaveTimed() // optimized without check, when maxDuration is infinite

        timeIsOver(stateCurrentTime - solve.requestIssuingInstant, solve.executionMaxDuration) ->
            sequenceOf(
                stateEndHalt(
                    TimeOutException(
                        "Given time for `${solve.query}` computation (${solve.executionMaxDuration}) wasn't enough for completion",
                        context = solve.context,
                        exceededDuration = solve.executionMaxDuration
                    )
                )
            )
        else -> behaveTimed()
    }

    /** Called only if executionTimeout has not been reached yet, and computation should go on */
    protected abstract fun behaveTimed(): Sequence<State>

    override fun getCurrentTime(): TimeInstant = stateCurrentTime

    /** A function to check if time for execution has ended */
    private fun timeIsOver(currentDuration: TimeDuration, maxDuration: TimeDuration) =
        currentDuration >= maxDuration

    override fun toString(): String = "${this::class} with $solve"
}
