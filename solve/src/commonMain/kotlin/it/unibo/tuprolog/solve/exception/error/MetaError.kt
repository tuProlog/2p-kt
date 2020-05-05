package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

class MetaError(
    message: String? = null,
    cause: TuPrologRuntimeException,
    context: ExecutionContext,
    extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    companion object {

        /** The type error Struct functor */
        const val typeFunctor = "meta_error"

        fun of(context: ExecutionContext, cause: PrologError) =
            MetaError(cause.message, cause, context, cause.errorStruct)

        fun of(context: ExecutionContext, cause: TuPrologRuntimeException) =
            when (cause) {
                is PrologError -> of(context, cause)
                else -> MetaError(cause.message, cause, context)
            }
    }

}