package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import org.gciatto.kt.math.BigDecimal

/**
 * Implementation of `sqrt/1` arithmetic functor
 *
 * @author Enrico
 */
object SquareRoot : UnaryMathFunction("sqrt") {
    override val help: String =
        """
        `sqrt(+Number)`
        
        Evaluates to the non-negative square root of `Number`, always as a real value. `Number` must be non-negative: the current implementation does not raise the standard `undefined` evaluation error for negative inputs, which are not handled.
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer.decimalValue)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real.value)

    /** Implements common behaviour for Integer and Real*/
    private fun commonBehaviour(decimal: BigDecimal): Real =
        // TODO: 25/10/2019 "undefined" checks missing (see the standard)
        Numeric.of(decimal.sqrt())
}
