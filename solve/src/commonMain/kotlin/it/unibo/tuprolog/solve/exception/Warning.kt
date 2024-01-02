package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class for Prolog warnings
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 *
 * @author Giovanni
 */
abstract class Warning(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
) : ResolutionException(message, cause, contexts) {
    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
    ) : this(message, cause, arrayOf(context))

    abstract override fun updateContext(
        newContext: ExecutionContext,
        index: Int,
    ): Warning

    abstract override fun updateLastContext(newContext: ExecutionContext): Warning

    abstract override fun pushContext(newContext: ExecutionContext): Warning
}
