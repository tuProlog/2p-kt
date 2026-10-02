package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.ArithmeticRelation

/** Implementation of '=:='/2 predicate */
object ArithmeticEqual : ArithmeticRelation<ExecutionContext>("=:=") {
    override val help: String =
        """
        `=:= (+Left, +Right)`
        
        Succeeds when the arithmetic values of `Left` and `Right` are numerically equal. Both arguments are evaluated as arithmetic expressions.
        """.trimIndent()

    override fun computeNumeric(
        x: Numeric,
        y: Numeric,
    ): Boolean = x.compareValueTo(y) == 0
}
