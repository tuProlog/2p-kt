package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solver
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads

/**
 * An exception that could occur during [Solver] execution
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 */
open class ResolutionException(
    message: String? = null,
    cause: Throwable? = null,
    @JsName("contexts") val contexts: Array<ExecutionContext>
) : TuPrologException(message, cause) {

    init {
        require(contexts.isNotEmpty())
    }

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        vararg otherContexts: ExecutionContext
    ) : this(message, cause, arrayOf(context, *otherContexts))

    constructor(cause: Throwable?, context: ExecutionContext) : this(cause?.toString(), cause, context)

    @JsName("context")
    val context: ExecutionContext
        get() = contexts[0]

    /** The exception stacktrace; shorthand for `context.prologStackTrace` */
    @JsName("prologStackTrace")
    val prologStackTrace: List<Struct>
        get() = when (contexts.size) {
            1 -> context.prologStackTrace
            else ->
                (
                    sequenceOf(*contexts).take(contexts.size - 1).flatMap { it.fullStackTraceButLast } +
                        contexts.last().fullStackTrace
                    ).toList()
        }

    private val ExecutionContext.fullStackTrace: Sequence<Struct>
        get() = prologStackTrace.asSequence()

    private val ExecutionContext.fullStackTraceButLast: Sequence<Struct>
        get() = prologStackTrace.run { slice(0 until lastIndex) }.asSequence()

    /**
     * Creates a new exception instance with the context with in position [index] updated to [newContext].
     * Subclasses should override this method and return the correct instance.
     */
    @JsName("updateContext")
    @JvmOverloads
    open fun updateContext(newContext: ExecutionContext, index: Int = 0): ResolutionException =
        ResolutionException(message, cause, contexts.setItem(index, newContext))

    @JsName("updateLastContext")
    open fun updateLastContext(newContext: ExecutionContext): ResolutionException =
        updateContext(newContext, contexts.lastIndex)

    @JsName("pushContext")
    open fun pushContext(newContext: ExecutionContext): ResolutionException =
        ResolutionException(message, cause, contexts.addLast(newContext))

    protected fun Array<ExecutionContext>.setItem(index: Int, item: ExecutionContext): Array<ExecutionContext> =
        copyOf().also { it[index] = item }

    protected fun Array<ExecutionContext>.addLast(item: ExecutionContext): Array<ExecutionContext> =
        arrayOf(*this, item)
}
