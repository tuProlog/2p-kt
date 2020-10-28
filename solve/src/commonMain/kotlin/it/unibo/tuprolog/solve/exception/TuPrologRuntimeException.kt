package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads

/**
 * An exception that could occur during Solver execution
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 */
open class TuPrologRuntimeException(
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
        get() = sequenceOf(*contexts).flatMap { it.prologStackTrace.asSequence() }.toList()

    /**
     * Creates a new exception instance with context field updated to [newContext].
     *
     * Subclasses should override this method and return the correct instance.
     */
    @JsName("updateContext")
    open fun updateContext(newContext: ExecutionContext): TuPrologRuntimeException =
        TuPrologRuntimeException(message, cause, newContext)

    @JsName("pushContext")
    open fun pushContext(newContext: ExecutionContext): TuPrologRuntimeException =
        TuPrologRuntimeException(message, cause, arrayOf(*contexts, context))

    protected fun Array<ExecutionContext>.setFirst(item: ExecutionContext): Array<ExecutionContext> =
        arrayOf(item, *copyOfRange(1, size))

    protected fun Array<ExecutionContext>.addLast(item: ExecutionContext): Array<ExecutionContext> =
        arrayOf(*this, item)
}
