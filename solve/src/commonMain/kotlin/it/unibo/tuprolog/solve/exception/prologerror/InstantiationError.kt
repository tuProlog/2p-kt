package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError

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
    context: ExecutionContext,
    extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    /** This constructor automatically fills [message] field with provided information */
    @Deprecated("Prefer InstantiationError.Companion.forArgument instead")
    constructor(context: ExecutionContext, procedure: Signature, index: Int? = null, variable: Var? = null) : this(
        message = "Argument ${index ?: ""} `${variable
            ?: ""}` of $procedure is unexpectedly not instantiated",
        context = context,
        extraData = variable
    )

    companion object {

        /** The instantiation error Struct functor */
        const val typeFunctor = "instantiation_error"

        fun forArgument(context: ExecutionContext, procedure: Signature, index: Int? = null, variable: Var? = null) =
            InstantiationError(
                message = "Argument ${index ?: ""} `${variable ?: ""}` of $procedure is unexpectedly not instantiated",
                context = context,
                extraData = variable
            )

        fun forGoal(context: ExecutionContext, procedure: Signature, variable: Var) =
            InstantiationError(
                message = "Uninstantiated subgoal ${variable} in procedure ${procedure}",
                context = context,
                extraData = variable
            )
    }
}
