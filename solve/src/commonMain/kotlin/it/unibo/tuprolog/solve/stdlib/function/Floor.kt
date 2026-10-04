package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.RoundingMode

/**
 * Implementation of `floor/1` arithmetic functor
 *
 * @author Enrico
 */
object Floor : UnaryMathFunction("floor") {
    override val help: String =
        """
        `floor(+Number)`
        
        Evaluates to the greatest integer not greater than `Number`.

        **Examples**

        ```prolog
        ?- X is floor(2.7).
        X = 2.

        ?- X is floor(-2.1).
        X = -3.
        ```
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer.decimalValue)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real.value)

    /** Implementation of common behaviour for Real and Integer */
    private fun commonBehaviour(decimal: BigDecimal): Integer =
        Numeric.of(decimal.setScale(0, RoundingMode.FLOOR).toBigInteger())
}
