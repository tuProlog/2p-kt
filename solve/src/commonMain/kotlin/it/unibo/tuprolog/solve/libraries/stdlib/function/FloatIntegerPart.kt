package it.unibo.tuprolog.solve.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.RoundingMode

/**
 * Implementation of `float_integer_part/1` arithmetic functor
 *
 * @author Enrico
 */
object FloatIntegerPart : UnaryMathFunction("float_integer_part") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real.decimalValue)

    /** Implements common behaviour for Integer and Real */
    private fun commonBehaviour(decimal: BigDecimal): Numeric = Numeric.of(
        when {
            decimal >= BigDecimal.ZERO -> decimal.setScale(0, RoundingMode.FLOOR)
            else -> -commonBehaviour(-decimal).decimalValue
        }
    )
}
