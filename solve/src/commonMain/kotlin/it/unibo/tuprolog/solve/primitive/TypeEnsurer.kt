package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

abstract class TypeEnsurer<E : ExecutionContext>(
    typeName: String,
) : UnaryPredicate.Predicative<E>(typeName) {
    override fun Solve.Request<E>.compute(first: Term): Boolean {
        ensureType(context, arguments[0][context.substitution])
        return true
    }

    abstract fun Solve.Request<E>.ensureType(
        context: E,
        term: Term,
    )
}
