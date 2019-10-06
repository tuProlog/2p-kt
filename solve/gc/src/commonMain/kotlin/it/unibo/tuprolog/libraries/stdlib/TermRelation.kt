package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class TermRelation(operator: String) : BinaryRelation(operator) {

    abstract class WithoutSideEffects(operator: String) : TermRelation(operator) {

        override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
                sequenceOf(
                        computeSingleResponse(request)
                )

        override fun computeSingleResponse(request: Solve.Request<ExecutionContext>): Solve.Response =
                request.replyWith(relationWithoutSideEffects(request.arguments[0], request.arguments[1]))

    }

    abstract class WithSideEffects(operator: String) : TermRelation(operator) {
        override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
                sequenceOf(
                        computeSingleResponse(request)
                )

        override fun computeSingleResponse(request: Solve.Request<ExecutionContext>): Solve.Response =
                when (val effects: Substitution = relationWithSideEffects(request.arguments[0], request.arguments[1])) {
                    is Substitution.Unifier -> request.replySuccess(effects)
                    else -> request.replyFail()
                }

    }
}