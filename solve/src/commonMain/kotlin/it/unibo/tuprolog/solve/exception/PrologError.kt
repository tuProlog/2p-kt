package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils.errorStructOf

/**
 * Base class for Standard Prolog Errors and possibly other custom Primitive errors
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param type The error type structure
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Giovanni
 * @author Enrico
 */
abstract class PrologError(
        override val message: String? = null,
        override val cause: Throwable? = null,
        override val context: ExecutionContext,
        open val type: Struct,
        open val extraData: Term? = null
) : TuPrologRuntimeException(message, cause, context) {

    /** The error Struct as described in Prolog standard: `error(error_type, error_extra)` */
    val errorStruct: Struct by lazy {
        extraData?.let { errorStructOf(type, it) }
                ?: errorStructOf(type)
    }

    constructor(cause: Throwable?, context: ExecutionContext, type: Struct, extraData: Term? = null)
            : this(cause?.toString(), cause, context, type, extraData)
}