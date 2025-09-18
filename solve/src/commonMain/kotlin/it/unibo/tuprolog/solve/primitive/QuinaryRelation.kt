package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

abstract class QuinaryRelation<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 5) {
    /** Template method aimed at computing the application of this relation to three [Term]s */
    protected abstract fun Solve.Request<E>.computeAll(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term,
        fifth: Term,
    ): Sequence<Solve.Response>

    final override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        request.computeAll(
            request.arguments[0],
            request.arguments[1],
            request.arguments[2],
            request.arguments[3],
            request.arguments[4],
        )

    abstract class WithoutSideEffects<E : ExecutionContext>(
        operator: String,
    ) : QuinaryRelation<E>(operator) {
        protected abstract fun Solve.Request<E>.computeAllSubstitutions(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Sequence<Substitution>

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Sequence<Solve.Response> = computeAllSubstitutions(first, second, third, fourth, fifth).map { replyWith(it) }
    }

    abstract class NonBacktrackable<E : ExecutionContext>(
        operator: String,
    ) : QuinaryRelation<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Solve.Response

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Sequence<Solve.Response> = sequenceOf(computeOne(first, second, third, fourth, fifth))
    }

    abstract class Functional<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOneSubstitution(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Substitution

        final override fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Solve.Response = replyWith(computeOneSubstitution(first, second, third, fourth, fifth))
    }

    abstract class Predicative<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.compute(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Boolean

        final override fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
            fifth: Term,
        ): Solve.Response = if (compute(first, second, third, fourth, fifth)) replySuccess() else replyFail()
    }
}
