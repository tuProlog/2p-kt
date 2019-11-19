package it.unibo.tuprolog.solve.exception.prologerror

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

    companion object {

        /** The system error Struct functor */
        const val typeFunctor = "system_error"
    }
}
