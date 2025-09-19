package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/** A base class to implement predicates with one argument */
abstract class UnaryPredicate<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 1) {
    /** Template method aimed at computing the application of this predicate to a [Term] */
    protected abstract fun Solve.Request<E>.computeAll(first: Term): Sequence<Solve.Response>

    final override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        request.computeAll(request.arguments[0])

    abstract class WithoutSideEffects<E : ExecutionContext>(
        operator: String,
    ) : UnaryPredicate<E>(operator) {
        protected abstract fun Solve.Request<E>.computeAllSubstitutions(first: Term): Sequence<Substitution>

        final override fun Solve.Request<E>.computeAll(first: Term): Sequence<Solve.Response> =
            computeAllSubstitutions(first).map {
                replyWith(it)
            }
    }

    abstract class NonBacktrackable<E : ExecutionContext>(
        operator: String,
    ) : UnaryPredicate<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOne(first: Term): Solve.Response

        final override fun Solve.Request<E>.computeAll(first: Term): Sequence<Solve.Response> =
            sequenceOf(computeOne(first))
    }

    abstract class Functional<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOneSubstitution(first: Term): Substitution

        final override fun Solve.Request<E>.computeOne(first: Term): Solve.Response =
            replyWith(computeOneSubstitution(first))
    }

    abstract class Predicative<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.compute(first: Term): Boolean

        final override fun Solve.Request<E>.computeOne(first: Term): Solve.Response =
            if (compute(first)) replySuccess() else replyFail()
    }
}
