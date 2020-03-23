package it.unibo.tuprolog.solve.library.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal
import kotlin.math.E
import kotlin.math.log

/**
 * Implementation of `log/1` arithmetic functor
 *
 * @author Enrico
 */
object NaturalLogarithm : UnaryMathFunction("log") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real.value)

    /** Implementation of common behaviour for Integer and Real */
    private fun commonBehaviour(decimal: BigDecimal): Real =
        Numeric.of(log(decimal.toDouble(), E))
}
