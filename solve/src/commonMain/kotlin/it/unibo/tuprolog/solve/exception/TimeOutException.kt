package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.solver.statemachine.TimeDuration

/**
 * Exception thrown if time for execution finished, before completion of solution process
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 *
 * @author Enrico
 */
internal class TimeOutException(
        override val message: String? = null,
        override val cause: Throwable? = null,
        override val context: ExecutionContext,
        val deltaTime: TimeDuration // TODO: 14/09/2019 what's the semantic of this field? how should be filled?
) : TuPrologRuntimeException(message, cause, context) {

    constructor(cause: Throwable?, context: ExecutionContext, deltaTime: TimeDuration)
            : this(cause?.toString(), cause, context, deltaTime)
}
