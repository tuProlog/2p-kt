package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * The system error occurs when an internal problem occurred and if not caught, it will halt inferential machine
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class SystemError constructor(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), extraData)

    override fun updateContext(newContext: ExecutionContext, index: Int): SystemError =
        SystemError(message, cause, contexts.setItem(index, newContext), extraData)

    override fun updateLastContext(newContext: ExecutionContext): SystemError =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): SystemError =
        SystemError(message, cause, contexts.addLast(newContext), extraData)

    companion object {

        /** The system error Struct functor */
        const val typeFunctor = "system_error"

        @JsName("forUncaughtKtException")
        @JvmStatic
        fun forUncaughtException(context: ExecutionContext, exception: Throwable): SystemError =
            message("Uncaught exception `${exception::class.simpleName}: ${exception.message}`") { m, extra ->
                SystemError(
                    message = m,
                    context = context,
                    extraData = extra
                )
            }

        @JsName("forUncaughtException")
        @JvmStatic
        fun forUncaughtException(context: ExecutionContext, exception: Term): SystemError =
            message("Uncaught exception `${exception.pretty()}`") { m, extra ->
                SystemError(
                    message = m,
                    context = context,
                    extraData = extra
                )
            }

        private fun PrologError.pretty(): String =
            when (this) {
                is MessageError -> content.pretty()
                else -> errorStruct.pretty()
            }

        @JsName("forUncaughtError")
        @JvmStatic
        fun forUncaughtError(exception: PrologError): SystemError =
            message("Uncaught exception `${exception.pretty()}`") { m, extra ->
                SystemError(m, exception, exception.contexts, extra)
            }
    }
}
