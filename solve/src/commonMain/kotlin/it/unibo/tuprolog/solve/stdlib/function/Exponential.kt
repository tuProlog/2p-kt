package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import org.gciatto.kt.math.BigDecimal
import kotlin.math.exp

/**
 * Implementation of `exp/1` arithmetic functor
 *
 * @author Enrico
 */
object Exponential : UnaryMathFunction("exp") {
    override val help: String =
        """
        `exp(+Number)`
        
        Evaluates to `e` (Euler's number) raised to `Number`, always as a real value. The current implementation does not raise the standard `float_overflow` evaluation error: results too large to be represented, e.g. `exp(1000)`, are not handled.
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer.decimalValue)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real.value)

    /** Implements the common behaviour for real and integer */
    private fun commonBehaviour(decimal: BigDecimal) =
        Numeric.of(
            exp(decimal.toDouble()),
        ) // TODO: 24/10/2019 missing "float_overflow" and "underflow" check (see the standard)
}
