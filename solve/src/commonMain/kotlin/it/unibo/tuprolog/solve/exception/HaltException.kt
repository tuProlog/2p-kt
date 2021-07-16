package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.js.JsName

/**
 * An exception thrown if there are problems during state machine execution, and solution process should be halted
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param exitStatus The integer code representing the exit status code; it defaults to 0
 *
 * @author Enrico
 */
class HaltException(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("exitStatus") val exitStatus: Int = 0
) : ResolutionException(message, cause, contexts) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        exitStatus: Int = 0
    ) : this(message, cause, arrayOf(context), exitStatus)

    constructor(cause: Throwable?, context: ExecutionContext, exitStatus: Int = 0) :
        this(cause?.toString(), cause, context, exitStatus)

    override fun updateContext(newContext: ExecutionContext, index: Int): HaltException =
        HaltException(message, cause, contexts.setItem(index, newContext), exitStatus)

    override fun updateLastContext(newContext: ExecutionContext): HaltException =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): HaltException =
        HaltException(message, cause, contexts.addLast(newContext), exitStatus)
}
