package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/** Base class to implement primitives that relate two [Term]s and provide a single response */
abstract class BinaryRelation<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 2) {
    /** Template method aimed at computing the application of this relation to three [Term]s */
    protected abstract fun Solve.Request<E>.computeAll(
        first: Term,
        second: Term,
    ): Sequence<Solve.Response>

    final override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        request.computeAll(request.arguments[0], request.arguments[1])

    abstract class WithoutSideEffects<E : ExecutionContext>(
        operator: String,
    ) : BinaryRelation<E>(operator) {
        protected abstract fun Solve.Request<E>.computeAllSubstitutions(
            first: Term,
            second: Term,
        ): Sequence<Substitution>

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
        ): Sequence<Solve.Response> = computeAllSubstitutions(first, second).map { replyWith(it) }
    }

    abstract class NonBacktrackable<E : ExecutionContext>(
        operator: String,
    ) : BinaryRelation<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
        ): Solve.Response

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
        ): Sequence<Solve.Response> = sequenceOf(computeOne(first, second))
    }

    abstract class Functional<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOneSubstitution(
            first: Term,
            second: Term,
        ): Substitution

        final override fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
        ): Solve.Response = replyWith(computeOneSubstitution(first, second))
    }

    abstract class Predicative<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.compute(
            first: Term,
            second: Term,
        ): Boolean

        final override fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
        ): Solve.Response = if (compute(first, second)) replySuccess() else replyFail()
    }
}
