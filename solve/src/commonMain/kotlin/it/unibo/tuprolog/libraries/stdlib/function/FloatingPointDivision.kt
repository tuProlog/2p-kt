package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal

/**
 * Implementation of `'/'/2` arithmetic functor
 *
 * @author Enrico
 */
object FloatingPointDivision : BinaryMathFunction("/") {

    override fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric =
            commonBehaviour(integer1.decimalValue, integer2.decimalValue, context)

    override fun mathFunction(real: Real, integer: Integer, context: ExecutionContext): Numeric =
            commonBehaviour(real.value, integer.decimalValue, context)

    override fun mathFunction(integer: Integer, real: Real, context: ExecutionContext): Numeric =
            commonBehaviour(integer.decimalValue, real.value, context)

    override fun mathFunction(real1: Real, real2: Real, context: ExecutionContext): Numeric =
            commonBehaviour(real1.value, real2.value, context)

    /** Implements common behaviour for Integer and Real */
    private fun commonBehaviour(dividend: BigDecimal, divisor: BigDecimal, context: ExecutionContext): Real =
            // TODO: 25/10/2019 "float_overflow" and "underflow" checks missing (see the standard)
            when (divisor) {
                BigDecimal.ZERO -> throwZeroDivisorError(context)
                else -> Numeric.of(dividend / divisor)
            }

}
