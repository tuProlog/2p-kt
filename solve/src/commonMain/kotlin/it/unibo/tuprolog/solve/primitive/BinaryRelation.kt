package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement primitives that relate two [Term]s and provide a single response, sparing implementers
 * from manually pulling `first`/`second` out of [Solve.Request.arguments] and checking the request's arity (always
 * 2, enforced by [PrimitiveWrapper]).
 *
 * This class and its nested subclasses form a ladder of increasingly specific (and increasingly convenient) template
 * methods to override, from most to least general:
 * - [BinaryRelation] itself -- override [computeAll] for a fully backtrackable relation producing arbitrarily many
 *   [Solve.Response]s (including side effects), e.g. via [Solve.Request.replyWith];
 * - [WithoutSideEffects] -- override `computeAllSubstitutions` for a backtrackable relation with no side effects,
 *   producing a [Sequence] of [Substitution]s (one per solution);
 * - [NonBacktrackable] -- override `computeOne` for a deterministic relation (no choice points) still able to
 *   attach side effects to its single [Solve.Response];
 * - [Functional] -- override `computeOneSubstitution` for a deterministic, side-effect-free relation producing a
 *   single [Substitution];
 * - [Predicative] -- override `compute` for a deterministic, pure test returning `true`/`false` (e.g. `==/2`).
 *
 * See the `it.unibo.tuprolog.solve.stdlib.primitive` implementations (e.g. arithmetic comparisons) for concrete
 * examples of each level.
 */
abstract class BinaryRelation<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 2) {
    /** Template method aimed at computing the application of this relation to [first] and [second]. */
    protected abstract fun Solve.Request<E>.computeAll(
        first: Term,
        second: Term,
    ): Sequence<Solve.Response>

    final override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        request.computeAll(request.arguments[0], request.arguments[1])

    /** A [BinaryRelation] that only needs to produce the [Substitution]s for each solution, without custom side effects. */
    abstract class WithoutSideEffects<E : ExecutionContext>(
        operator: String,
    ) : BinaryRelation<E>(operator) {
        /** Template method producing one [Substitution] per solution of relating [first] and [second]. */
        protected abstract fun Solve.Request<E>.computeAllSubstitutions(
            first: Term,
            second: Term,
        ): Sequence<Substitution>

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
        ): Sequence<Solve.Response> = computeAllSubstitutions(first, second).map { replyWith(it) }
    }

    /** A [BinaryRelation] known to be deterministic, i.e. producing exactly one [Solve.Response] (no choice points). */
    abstract class NonBacktrackable<E : ExecutionContext>(
        operator: String,
    ) : BinaryRelation<E>(operator) {
        /** Template method producing the single [Solve.Response] for relating [first] and [second]. */
        protected abstract fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
        ): Solve.Response

        final override fun Solve.Request<E>.computeAll(
            first: Term,
            second: Term,
        ): Sequence<Solve.Response> = sequenceOf(computeOne(first, second))
    }

    /** A [NonBacktrackable] [BinaryRelation] that only needs to produce a single [Substitution], without custom side effects. */
    abstract class Functional<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        /** Template method producing the single [Substitution] for relating [first] and [second]. */
        protected abstract fun Solve.Request<E>.computeOneSubstitution(
            first: Term,
            second: Term,
        ): Substitution

        final override fun Solve.Request<E>.computeOne(
            first: Term,
            second: Term,
        ): Solve.Response = replyWith(computeOneSubstitution(first, second))
    }

    /** A [NonBacktrackable] [BinaryRelation] that is a pure success/failure test, with no substitution nor side effects. */
    abstract class Predicative<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        /** Template method testing whether the relation holds between [first] and [second]. */
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
