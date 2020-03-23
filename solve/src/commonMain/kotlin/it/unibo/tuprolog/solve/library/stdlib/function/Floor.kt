package it.unibo.tuprolog.solve.library.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.RoundingMode

/**
 * Implementation of `floor/1` arithmetic functor
 *
 * @author Enrico
 */
object Floor : UnaryMathFunction("floor") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real.value)

    /** Implementation of common behaviour for Real and Integer */
    private fun commonBehaviour(decimal: BigDecimal): Integer =
        Numeric.of(decimal.setScale(0, RoundingMode.FLOOR).toBigInteger())
}
