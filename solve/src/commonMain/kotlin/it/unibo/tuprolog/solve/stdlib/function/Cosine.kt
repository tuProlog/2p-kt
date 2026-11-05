package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.big.cos

/**
 * Implementation of `cos/1` arithmetic functor
 *
 * @author Enrico
 */
object Cosine : UnaryMathFunction("cos") {
    override val help: String =
        """
        `cos(+Number)`
        
        Evaluates to the cosine of `Number`, interpreted as an angle in radians. The result is always a real value.

        **Examples**

        ```prolog
        ?- X is cos(0).
        X = 1.0.

        ?- X is cos(0.0).
        X = 1.0.
        ```
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer.decimalValue, context)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real.value, context)

    /** Implements the common behaviour for real and integer */
    private fun commonBehaviour(
        decimal: BigDecimal,
        context: ExecutionContext,
    ) = computeReal(context) { mc -> relativelyPrecise(mc) { decimal.cos(it) } }
}
