package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.js.JsName

/**
 * An exception thrown if there are problems during state machine execution, and solution process should be halted
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param exitStatus The integer code representing the exit status code; it defaults to 1
 *
 * @author Enrico
 */
class HaltException(
    message: String? = null,
    cause: Throwable? = null,
    context: ExecutionContext,
    @JsName("exitStatus") val exitStatus: Int = 1
) : TuPrologRuntimeException(message, cause, context) {

    constructor(cause: Throwable?, context: ExecutionContext, exitStatus: Int = 1)
            : this(cause?.toString(), cause, context, exitStatus)

    override fun updateContext(newContext: ExecutionContext): HaltException =
        HaltException(message, cause, newContext, exitStatus)
}
