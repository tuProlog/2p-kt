package it.unibo.tuprolog.solve.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal
import kotlin.math.exp

/**
 * Implementation of `exp/1` arithmetic functor
 *
 * @author Enrico
 */
object Exponential : UnaryMathFunction("exp") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real.value)

    /** Implements the common behaviour for real and integer */
    private fun commonBehaviour(decimal: BigDecimal) =
        Numeric.of(exp(decimal.toDouble())) // TODO: 24/10/2019 missing "float_overflow" and "underflow" check (see the standard)
}
