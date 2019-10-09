package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * An exception that could occur during Solver execution
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 */
open class TuPrologRuntimeException(
        message: String? = null,
        cause: Throwable? = null,
        val context: ExecutionContext
) : TuPrologException(message, cause) {

    constructor(cause: Throwable?, context: ExecutionContext) : this(cause?.toString(), cause, context)

    /** The exception stacktrace; shorthand for `context.prologStackTrace` */
    val prologStackTrace: Sequence<Struct> by lazy { context.prologStackTrace }

    open fun updateContext(context: ExecutionContext): TuPrologRuntimeException {
        return TuPrologRuntimeException(message, cause, context)
    }
}
