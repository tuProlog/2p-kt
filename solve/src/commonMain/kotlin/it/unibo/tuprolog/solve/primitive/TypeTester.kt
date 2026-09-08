package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class for ISO type-checking predicates of arity 1 (e.g. `atom/1`, `var/1`, `is_list/1`): succeeds if
 * [testType] returns `true` for the (dereferenced) argument, fails (rather than throwing) otherwise. Contrast with
 * [TypeEnsurer], which throws instead of failing.
 */
abstract class TypeTester<E : ExecutionContext>(
    typeName: String,
) : UnaryPredicate.Predicative<E>(typeName) {
    override fun Solve.Request<E>.compute(first: Term): Boolean = testType(arguments[0][context.substitution])

    /** Template method testing whether [term] has the expected shape/type. */
    abstract fun testType(term: Term): Boolean
}
