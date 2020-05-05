package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.RoundingMode

/**
 * Implementation of `truncate/1` arithmetic functor
 *
 * @author Enrico
 */
object Truncate : UnaryMathFunction("truncate") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        // TODO: 25/10/2019 "int_overflow" check missing (see the standard)
        commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real.value)

    /** Implements common behaviour for Real and Integer */
    private fun commonBehaviour(decimal: BigDecimal): Integer =
        Numeric.of(decimal.setScale(0, RoundingMode.DOWN).toBigInteger())
}
