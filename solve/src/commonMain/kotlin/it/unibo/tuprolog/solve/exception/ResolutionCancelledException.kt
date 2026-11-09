package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.SolutionStream

/**
 * The exception of the [it.unibo.tuprolog.solve.Solution.Halt] that ends a resolution stopped through
 * [SolutionStream.cancel] (#1010).
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 */
class ResolutionCancelledException(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
) : ResolutionException(message, cause, contexts) {
    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
    ) : this(message, cause, arrayOf(context))

    override fun updateContext(
        newContext: ExecutionContext,
        index: Int,
    ): ResolutionCancelledException = ResolutionCancelledException(message, cause, contexts.setItem(index, newContext))

    override fun updateLastContext(newContext: ExecutionContext): ResolutionCancelledException =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): ResolutionCancelledException =
        ResolutionCancelledException(message, cause, arrayOf(*contexts, newContext))
}
