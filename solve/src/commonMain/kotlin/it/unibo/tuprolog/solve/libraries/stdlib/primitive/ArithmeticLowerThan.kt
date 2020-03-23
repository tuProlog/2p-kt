package it.unibo.tuprolog.solve.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext

/** Implementation of '<'/2 predicate */
object ArithmeticLowerThan : ArithmeticRelation<ExecutionContext>("<") {
    override fun arithmeticRelation(x: Numeric, y: Numeric): Boolean =
        x < y
}
