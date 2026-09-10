package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement primitives that relate four [Term]s, sparing implementers from manually pulling
 * `first`/`second`/`third`/`fourth` out of [Solve.Request.arguments]. Follows the same design (and same ladder of
 * nested subclasses -- [WithoutSideEffects], [NonBacktrackable], [Functional], [Predicative]) as [BinaryRelation];
 * see its KDoc for the full rationale.
 */
abstract class QuaternaryRelation<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 4) {
    /** Template method aimed at computing the application of this relation to [first], [second], [third] and [fourth]. */
    protected abstract fun Solve.Request<E>.computeAll(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term,
    ): Sequence<Solve.Response>

    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        request.computeAll(
            request.arguments[0],
            request.arguments[1],
            request.arguments[2],
            request.arguments[3],
        )

    /** See [BinaryRelation.WithoutSideEffects]. */
    abstract class WithoutSideEffects<E : ExecutionContext>(
        operator: String,
    ) : QuaternaryRelation<E>(operator) {
        protected abstract fun Solve.Request<E>.computeAllSubstitutions(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
        ): Sequence<Substitution>

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
        ): Sequence<Solve.Response> = computeAllSubstitutions(first, second, third, fourth).map { replyWith(it) }
    }

    /** See [BinaryRelation.NonBacktrackable]. */
    abstract class NonBacktrackable<E : ExecutionContext>(
        operator: String,
    ) : QuaternaryRelation<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
        ): Solve.Response

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
        ): Sequence<Solve.Response> = sequenceOf(computeOne(first, second, third, fourth))
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
        ): Substitution

        final override fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
        ): Solve.Response = replyWith(computeOneSubstitution(first, second, third, fourth))
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
        ): Boolean

        final override fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
            third: Term,
            fourth: Term,
        ): Solve.Response = if (compute(first, second, third, fourth)) replySuccess() else replyFail()
    }
}
