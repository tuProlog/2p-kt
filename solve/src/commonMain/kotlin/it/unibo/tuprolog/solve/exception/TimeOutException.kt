package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeDuration
import kotlin.js.JsName

/**
 * Exception thrown if time for execution finished, before completion of solution process
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param exceededDuration The time duration exceeded
 *
 * @author Enrico
 */
class TimeOutException(
    message: String? = null,
    cause: Throwable? = null,
    context: ExecutionContext,
    @JsName("exceededDuration") val exceededDuration: TimeDuration
) : TuPrologRuntimeException(message, cause, context) {

    constructor(cause: Throwable?, context: ExecutionContext, exceededDuration: TimeDuration)
            : this(cause?.toString(), cause, context, exceededDuration)

    override fun updateContext(newContext: ExecutionContext): TimeOutException =
        TimeOutException(message, cause, newContext, exceededDuration)
}
