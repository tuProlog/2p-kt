package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.ArithmeticRelation

/** Implementation of '>='/2 predicate */
object ArithmeticGreaterThanOrEqualTo : ArithmeticRelation<ExecutionContext>(">=") {
    override fun arithmeticRelation(x: Numeric, y: Numeric): Boolean =
        x.compareValueTo(y) >= 0
}
