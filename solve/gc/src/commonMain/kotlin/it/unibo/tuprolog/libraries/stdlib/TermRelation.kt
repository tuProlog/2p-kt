package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solve

abstract class TermRelation(operator: String) : BinaryRelation(operator) {

    abstract class WithoutSideEffects(operator: String) : TermRelation(operator) {

        override fun uncheckedImplementation(request: Solve.Request): Sequence<Solve.Response> =
                sequenceOf(
                        computeSingleResponse(request)
                )

        override fun computeSingleResponse(request: Solve.Request): Solve.Response {
            return request.toResponse(relationWithoutSideEffects(request.arguments[0], request.arguments[1]))
        }

    }

    abstract class WithSideEffects(operator: String) : TermRelation(operator) {
        override fun uncheckedImplementation(request: Solve.Request): Sequence<Solve.Response> =
                sequenceOf(
                        computeSingleResponse(request)
                )

        override fun computeSingleResponse(request: Solve.Request): Solve.Response {
            return when (val effects: Substitution = relationWithSideEffects(request.arguments[0], request.arguments[1])) {
                is Substitution.Unifier -> request.toSuccessfulResponse(effects)
                else -> request.toFailingResponse()
            }
        }

    }
}