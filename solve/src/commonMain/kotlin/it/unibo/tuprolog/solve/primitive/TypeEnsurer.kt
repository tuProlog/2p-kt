package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class for unary predicates (arity 1) that always succeed if their argument is of the expected type, and
 * throw otherwise, rather than merely testing and failing on mismatch (contrast with [TypeTester]). [ensureType] is
 * expected to throw (typically a [it.unibo.tuprolog.solve.exception.error.TypeError]) on a mismatching `term`, and
 * return normally otherwise.
 */
abstract class TypeEnsurer<E : ExecutionContext>(
    typeName: String,
) : UnaryPredicate.Predicative<E>(typeName) {
    override fun Solve.Request<E>.compute(first: Term): Boolean {
        ensureType(context, arguments[0][context.substitution])
        return true
    }

    /** Template method expected to throw if `term` does not have the expected type, and return normally otherwise. */
    abstract fun Solve.Request<E>.ensureType(
        context: E,
        term: Term,
    )
}
