package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import org.gciatto.kt.math.BigDecimal
import kotlin.math.cos

/**
 * Implementation of `cos/1` arithmetic functor
 *
 * @author Enrico
 */
object Cosine : UnaryMathFunction("cos") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        commonBehaviour(integer.decimalValue)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        commonBehaviour(real.value)

    /** Implements the common behaviour for real and integer */
    private fun commonBehaviour(decimal: BigDecimal) =
        Numeric.of(cos(decimal.toDouble()))
}
