package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class TermRelation(operator: String) : BinaryRelation(operator) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
            sequenceOf(request.computeSingleResponse())

    abstract class WithoutSideEffects(operator: String) : TermRelation(operator) {

        override fun Solve.Request<ExecutionContext>.computeSingleResponse(): Solve.Response =
                replyWith(relationWithoutSideEffects(arguments[0], arguments[1]))
    }

    abstract class WithSideEffects(operator: String) : TermRelation(operator) {

        override fun Solve.Request<ExecutionContext>.computeSingleResponse(): Solve.Response =
                when (val effects: Substitution = relationWithSideEffects(arguments[0], arguments[1])) {
                    is Substitution.Unifier -> replySuccess(effects)
                    else -> replyFail()
                }
    }
}
