package it.unibo.tuprolog.solve.library.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext

/** Implementation of '=\='/2 predicate */
object ArithmeticNotEqual : ArithmeticRelation<ExecutionContext>("=\\=") {
    override fun arithmeticRelation(x: Numeric, y: Numeric): Boolean =
        x.compareTo(y) != 0
}
