package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.solver.DeclarativeImplExecutionContext

/**
 * The instantiation error occurs when some Term is a Variable, and it should not
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class InstantiationError(
        message: String? = null,
        cause: Throwable? = null,
        context: DeclarativeImplExecutionContext,
        extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    companion object {

        /** The instantiation error Struct functor */
        const val typeFunctor = "instantiation_error"
    }
}
