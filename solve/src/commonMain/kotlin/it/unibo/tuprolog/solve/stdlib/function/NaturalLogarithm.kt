package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import org.gciatto.kt.math.BigDecimal
import kotlin.math.E
import kotlin.math.log

/**
 * Implementation of `log/1` arithmetic functor
 *
 * @author Enrico
 */
object NaturalLogarithm : UnaryMathFunction("log") {
    override val help: String =
        """
        `log(+Number)`
        
        Evaluates to the natural (base `e`) logarithm of `Number`, always as a real value. `Number` must be positive: the current implementation does not raise the standard `undefined` evaluation error for zero or negative inputs, which are not handled.
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer.decimalValue)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real.value)

    /** Implementation of common behaviour for Integer and Real */
    private fun commonBehaviour(decimal: BigDecimal): Real = Numeric.of(log(decimal.toDouble(), E))
}
