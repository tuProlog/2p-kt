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
        context: ExecutionContext? = null,
        extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    constructor(variable: Var, procedure: Signature)
            : this(
            message="Variable $variable unexpectedly uninstantiated in $procedure",
            extraData = Atom.of("Variable $variable unexpectedly uninstantiated in $procedure")
    )

    constructor(index: Int, variable: Var, procedure: Signature)
            : this(
            message="Argument $index ($variable) of $procedure is unexpectedly uninstantiated",
            extraData = Atom.of("Argument $index ($variable) of $procedure is unexpectedly uninstantiated")
    )

    constructor(index: Int, procedure: Signature)
            : this(
            message="Argument $index of $procedure is unexpectedly uninstantiated",
            extraData = Atom.of("Argument $index of $procedure is unexpectedly uninstantiated")
    )

    companion object {

        /** The instantiation error Struct functor */
        const val typeFunctor = "instantiation_error"
    }
}
