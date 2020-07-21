package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.function.ExpressionEvaluator

/** Base class for implementing arithmetic relation between [Term] terms */
abstract class StandardOrderRelation<E : ExecutionContext>(operator: String) : BinaryRelation<E>(operator) {

    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        sequenceOf(
            request.ensuringAllArgumentsAreInstantiated()
                .computeSingleResponse()
        )

    override fun Solve.Request<E>.computeSingleResponse(): Solve.Response =
            replyWith(relationWithoutSideEffects(arguments[0], arguments[1]))


    override fun relationWithoutSideEffects(x: Term, y: Term): Boolean =
        standardOrderfunction(x, y)

    /** Template method that should implement the standard order relation between [x] and [y] */
    protected abstract fun standardOrderfunction(x: Term, y: Term): Boolean
}