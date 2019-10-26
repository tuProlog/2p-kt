package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext

/** Implementation of '>='/2 predicate */
object ArithmeticGreaterThanOrEqualTo : ArithmeticRelation<ExecutionContext>(">=") {
    override fun arithmeticRelation(x: Numeric, y: Numeric): Boolean =
            x >= y
}
