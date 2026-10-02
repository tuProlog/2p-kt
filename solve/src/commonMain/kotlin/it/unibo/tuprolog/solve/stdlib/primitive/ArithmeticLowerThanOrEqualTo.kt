package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.ArithmeticRelation

/** Implementation of '=<'/2 predicate */
object ArithmeticLowerThanOrEqualTo : ArithmeticRelation<ExecutionContext>("=<") {
    override val help: String =
        """
        `=<(+Left, +Right)`
        
        Succeeds when the arithmetic value of `Left` is less than or equal to the arithmetic value of `Right`.
        """.trimIndent()

    override fun computeNumeric(
        x: Numeric,
        y: Numeric,
    ): Boolean = x.compareValueTo(y) <= 0
}
