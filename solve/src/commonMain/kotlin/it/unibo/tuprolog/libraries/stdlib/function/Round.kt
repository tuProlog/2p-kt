package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.RoundingMode

/**
 * Implementation of `round/1` arithmetic functor
 *
 * @author Enrico
 */
object Round : UnaryMathFunction("round") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real.value)

    /** Implements common behaviour for Integer and Real */
    private fun commonBehaviour(decimal: BigDecimal): Integer =
        // TODO: 25/10/2019 "int_overflow" check missing (see the standard)
        Numeric.of(decimal.setScale(0, RoundingMode.HALF_UP).toBigInteger())
}
