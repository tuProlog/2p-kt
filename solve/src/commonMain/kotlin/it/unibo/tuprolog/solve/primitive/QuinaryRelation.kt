package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement primitives that relate five [Term]s, sparing implementers from manually pulling
 * `first`/`second`/`third`/`fourth`/`fifth` out of [Solve.Request.arguments]. Follows the same design (and same
 * ladder of nested subclasses -- [WithoutSideEffects], [NonBacktrackable], [Functional], [Predicative]) as
 * [BinaryRelation]; see its KDoc for the full rationale.
 */
abstract class QuinaryRelation<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 5) {
    /** Template method aimed at computing the application of this relation to [first], [second], [third], [fourth] and [fifth]. */
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

    /** See [BinaryRelation.WithoutSideEffects]. */
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

    /** See [BinaryRelation.NonBacktrackable]. */
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

    /** See [BinaryRelation.Functional]. */
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

    /** See [BinaryRelation.Predicative]. */
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
