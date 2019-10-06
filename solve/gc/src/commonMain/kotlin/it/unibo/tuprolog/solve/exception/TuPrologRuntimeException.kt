package it.unibo.tuprolog.solve.exception

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
        open var context: ExecutionContext? = null
) : TuPrologException(message, cause) {

    constructor(cause: Throwable?, context: ExecutionContext?) : this(cause?.toString(), cause, context)

    val activationRecords: Sequence<ExecutionContext>
        get() = this.context?.pathToRoot?.filter { it.isActivationRecord } ?: emptySequence()
}