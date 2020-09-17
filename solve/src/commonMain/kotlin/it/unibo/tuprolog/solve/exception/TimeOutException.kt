package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeDuration
import kotlin.js.JsName

/**
 * Exception thrown if time for execution finished, before completion of solution process
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param exceededDuration The time duration exceeded
 *
 * @author Enrico
 */
class TimeOutException(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("exceededDuration") val exceededDuration: TimeDuration
) : TuPrologRuntimeException(message, cause, contexts) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        exceededDuration: TimeDuration
    ) : this(message, cause, arrayOf(context), exceededDuration)

    constructor(cause: Throwable?, context: ExecutionContext, exceededDuration: TimeDuration) :
        this(cause?.toString(), cause, context, exceededDuration)

    override fun updateContext(newContext: ExecutionContext): TimeOutException =
        TimeOutException(message, cause, contexts.setFirst(newContext), exceededDuration)

    override fun pushContext(newContext: ExecutionContext): TimeOutException =
        TimeOutException(message, cause, arrayOf(*contexts, newContext), exceededDuration)
}
