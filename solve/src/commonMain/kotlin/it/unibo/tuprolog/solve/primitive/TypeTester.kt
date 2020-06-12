package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class TypeTester<E : ExecutionContext>(typeName: String) : UnaryPredicate.Predicative<E>(typeName) {
    override fun Solve.Request<E>.compute(first: Term): Boolean {
        return testType(arguments[0][context.substitution])
    }

    abstract fun testType(term: Term): Boolean
}