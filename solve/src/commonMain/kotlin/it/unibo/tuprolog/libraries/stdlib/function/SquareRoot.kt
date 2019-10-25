package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal

/**
 * Implementation of `sqrt/1` arithmetic functor
 *
 * @author Enrico
 */
object SquareRoot : UnaryMathFunction("sqrt") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
            commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
            commonBehaviour(real.value)

    /** Implements common behaviour for Integer and Real*/
    private fun commonBehaviour(decimal: BigDecimal): Real =
            // TODO: 25/10/2019 "undefined" checks missing (see the standard)
            Numeric.of(decimal.sqrt())
}
