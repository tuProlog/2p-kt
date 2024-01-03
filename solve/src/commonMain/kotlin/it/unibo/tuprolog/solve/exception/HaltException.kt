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
    @JsName("exitStatus") val exitStatus: Int = 0,
    message: String? = "Resolution has been halted with exit code $exitStatus",
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
) : ResolutionException(message, cause, contexts) {
    constructor(
        exitStatus: Int = 0,
        message: String? = "Resolution has been halted with exit code $exitStatus",
        cause: Throwable? = null,
        context: ExecutionContext,
    ) : this(exitStatus, message, cause, arrayOf(context))

    constructor(exitStatus: Int = 0, cause: Throwable?, context: ExecutionContext) :
        this(exitStatus, cause?.toString(), cause, context)

    override fun updateContext(
        newContext: ExecutionContext,
        index: Int,
    ): HaltException = HaltException(exitStatus, message, cause, contexts.setItem(index, newContext))

    override fun updateLastContext(newContext: ExecutionContext): HaltException =
        updateContext(
            newContext,
            contexts.lastIndex,
        )

    override fun pushContext(newContext: ExecutionContext): HaltException =
        HaltException(exitStatus, message, cause, contexts.addLast(newContext))
}
