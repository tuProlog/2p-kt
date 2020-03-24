package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class for Prolog warnings
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 *
 * @author Giovanni
 */
abstract class PrologWarning(
    message: String? = null,
    cause: Throwable? = null,
    context: ExecutionContext
) : TuPrologRuntimeException(message, cause, context)