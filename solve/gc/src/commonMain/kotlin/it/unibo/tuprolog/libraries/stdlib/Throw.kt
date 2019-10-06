package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

object Throw : UnaryPredicate("throw") {
    override fun uncheckedImplementation(request: Solve.Request): Sequence<Solve.Response> =
            sequenceOf(
                    ensuringAllArgumentsAreInstantiated(request) {
                        ensuringAllArgumentsAreStructs(it) {
                            computeSingleResponse(it)
                        }
                    }
            )

    override fun computeSingleResponse(request: Solve.Request): Solve.Response {
        return request.toExceptionalResponse(handleError(request.arguments[0] as Struct))
    }

    private fun handleError(error: Struct): TuPrologRuntimeException {
        return when {
            error.functor == "error" && error.arity <= 2 -> {
                PrologError.of(type = error[0] as Struct, extraData = if (error.arity > 1) error[1] else null)
            }
            else -> PrologError.of(type = error)
        }
    }
}