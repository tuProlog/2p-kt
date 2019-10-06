package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext

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
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext? = null,
        val deltaTime: Long // TODO: 14/09/2019 what's the semantic of this field? how should be filled?
) : TuPrologRuntimeException(message, cause, context) {

    constructor(cause: Throwable?, context: ExecutionContext?, deltaTime: Long)
            : this(cause?.toString(), cause, context, deltaTime)

}
