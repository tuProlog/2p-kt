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
abstract class PrologWarning(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>
) : TuPrologRuntimeException(message, cause, contexts) {
    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext
    ) : this(message, cause, arrayOf(context))

    abstract override fun updateContext(newContext: ExecutionContext): PrologWarning

    abstract override fun pushContext(newContext: ExecutionContext): PrologWarning
}