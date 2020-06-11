package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/** Base class for implementing arithmetic relation between [Numeric] terms */
abstract class ArithmeticRelation<E : ExecutionContext>(operator: String) : BinaryRelation.Predicative<E>(operator) {

    override fun Solve.Request<E>.compute(first: Term, second: Term): Boolean {
        ensuringArgumentIsNumeric(1)
        ensuringArgumentIsNumeric(2)
        return arithmeticRelation(first as Numeric, second as Numeric)
    }

    abstract fun arithmeticRelation(x: Numeric, y: Numeric): Boolean
}
