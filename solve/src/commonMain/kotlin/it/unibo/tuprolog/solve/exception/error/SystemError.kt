package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError

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

    override fun updateContext(newContext: ExecutionContext): SystemError =
        SystemError(message, cause, contexts.setFirst(newContext), extraData)

    override fun pushContext(newContext: ExecutionContext): SystemError =
        SystemError(message, cause, contexts.addLast(newContext), extraData)

    companion object {

        /** The system error Struct functor */
        const val typeFunctor = "system_error"

        fun forUncaughtException(context: ExecutionContext, exception: Term): SystemError =
            message("Uncaught exception `${exception.pretty()}`") { m, extra ->
                SystemError(
                    message = m,
                    context = context,
                    extraData = extra
                )
            }

        fun forUncaughtException(context: ExecutionContext, exception: PrologError): SystemError =
            when (exception) {
                is MessageError -> forUncaughtException(context, exception.content)
                else -> forUncaughtException(context, exception.errorStruct)
            }
    }
}
