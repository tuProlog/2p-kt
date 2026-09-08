package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * A base class to implement predicates with zero arguments (e.g. `!/0`, `nl/0`, `repeat/0`). Follows the same
 * design (and same ladder of nested subclasses -- [WithoutSideEffects], [NonBacktrackable], [Functional],
 * [Predicative]) as [BinaryRelation]; see its KDoc for the full rationale. Also available as [ZeroaryPredicate].
 */
abstract class PredicateWithoutArguments<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 0) {
    /** Template method that should compute the response(s) of invoking this zero-argument predicate. */
    protected abstract fun Solve.Request<E>.computeAll(): Sequence<Solve.Response>

    final override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        request.computeAll()

    /** See [BinaryRelation.WithoutSideEffects]. */
    abstract class WithoutSideEffects<E : ExecutionContext>(
        operator: String,
    ) : PredicateWithoutArguments<E>(operator) {
        protected abstract fun Solve.Request<E>.computeAllSubstitutions(): Sequence<Substitution>

        final override fun Solve.Request<E>.computeAll(): Sequence<Solve.Response> =
            computeAllSubstitutions().map {
                replyWith(it)
            }
    }

    /** See [BinaryRelation.NonBacktrackable]. */
    abstract class NonBacktrackable<E : ExecutionContext>(
        operator: String,
    ) : PredicateWithoutArguments<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOne(): Solve.Response

        final override fun Solve.Request<E>.computeAll(): Sequence<Solve.Response> = sequenceOf(computeOne())
    }

    /** See [BinaryRelation.Functional]. */
    abstract class Functional<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOneSubstitution(): Substitution

        final override fun Solve.Request<E>.computeOne(): Solve.Response = replyWith(computeOneSubstitution())
    }

    /** See [BinaryRelation.Predicative]. */
    abstract class Predicative<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.compute(): Boolean

        final override fun Solve.Request<E>.computeOne(): Solve.Response =
            if (compute()) replySuccess() else replyFail()
    }
}
