package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.BinaryMathFunction
import org.gciatto.kt.math.BigDecimal

/**
 * Implementation of `'+'/2` arithmetic functor
 *
 * @author Enrico
 */
object Addition : BinaryMathFunction("+") {

    override fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric =
        Numeric.of(integer1.value + integer2.value) // TODO: 24/10/2019 "int_overflow" check missing (see the standard)

    override fun mathFunction(real: Real, integer: Integer, context: ExecutionContext): Numeric =
        commonBehaviour(real.value, integer.decimalValue)

    override fun mathFunction(integer: Integer, real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(integer.decimalValue, real.value)

    override fun mathFunction(real1: Real, real2: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real1.value, real2.value)

    /** Method implementing the behaviour when the two numbers are of different types */
    private fun commonBehaviour(decimal1: BigDecimal, decimal2: BigDecimal) =
        Numeric.of(decimal1 + decimal2) // TODO: 24/10/2019 "float_overflow" and "underflow" checks missing (see the standard)
}
