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
 * @param context The current context at exception creation
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class SystemError(
    message: String? = null,
    cause: Throwable? = null,
    context: ExecutionContext,
    extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    override fun updateContext(newContext: ExecutionContext): PrologError = // TODO: 21/01/2020 since PrologError already correctly implements updateContext for all of its subtypes, this is not needed
        SystemError(message, cause, newContext, extraData)

    companion object {

        /** The system error Struct functor */
        const val typeFunctor = "system_error"

        // TODO: 16/01/2020 test factories

        fun forUncaughtException(context: ExecutionContext, exception: Term): SystemError =
            "Uncaught exception `$exception`".let {
                SystemError(
                    message = it,
                    context = context,
                    extraData = Atom.of(it)
                )
            }

        fun forUncaughtException(context: ExecutionContext, exception: PrologError): SystemError =
            when(exception) {
                is MessageError -> forUncaughtException(context, exception.content)
                else -> forUncaughtException(context, exception.errorStruct)
            }

    }
}
