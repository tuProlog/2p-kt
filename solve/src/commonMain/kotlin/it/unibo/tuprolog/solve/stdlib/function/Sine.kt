package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import org.gciatto.kt.math.BigDecimal
import kotlin.math.sin

/**
 * Implementation of `sin/1` arithmetic functor
 *
 * @author Enrico
 */
object Sine : UnaryMathFunction("sin") {
    override val help: String =
        """
        `sin(+Number)`
        
        Evaluates to the sine of `Number`, interpreted as an angle in radians. The result is always a real value.
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer.decimalValue)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real.value)

    /** Implementation of common behaviour for Integer and Real */
    private fun commonBehaviour(decimal: BigDecimal) = Numeric.of(sin(decimal.toDouble()))
}
