package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/** A base class to implement predicates with zero argument */
abstract class PredicateWithoutArguments<E : ExecutionContext>(
    operator: String,
) : PrimitiveWrapper<E>(operator, 0) {
    /** Template method that should compute the response of the application of this predicate to a term [Term] */
    protected abstract fun Solve.Request<E>.computeAll(): Sequence<Solve.Response>

    final override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        request.computeAll()

    abstract class WithoutSideEffects<E : ExecutionContext>(
        operator: String,
    ) : PredicateWithoutArguments<E>(operator) {
        protected abstract fun Solve.Request<E>.computeAllSubstitutions(): Sequence<Substitution>

        final override fun Solve.Request<E>.computeAll(): Sequence<Solve.Response> =
            computeAllSubstitutions().map {
                replyWith(it)
            }
    }

    abstract class NonBacktrackable<E : ExecutionContext>(
        operator: String,
    ) : PredicateWithoutArguments<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOne(): Solve.Response

        final override fun Solve.Request<E>.computeAll(): Sequence<Solve.Response> = sequenceOf(computeOne())
    }

    abstract class Functional<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.computeOneSubstitution(): Substitution

        final override fun Solve.Request<E>.computeOne(): Solve.Response = replyWith(computeOneSubstitution())
    }

    abstract class Predicative<E : ExecutionContext>(
        operator: String,
    ) : NonBacktrackable<E>(operator) {
        protected abstract fun Solve.Request<E>.compute(): Boolean

        final override fun Solve.Request<E>.computeOne(): Solve.Response =
            if (compute()) replySuccess() else replyFail()
    }
}
