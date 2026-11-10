package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction
import it.unibo.tuprolog.solve.function.realMathContext
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
        
        Evaluates to the non-negative square root of `Number`, always as a real value, rounded to `real_precision_digits` digits whatever `real_precision_mode` is (an exact root, such as `sqrt(2.25)`, stays exact). `Number` must be non-negative, otherwise the `undefined` evaluation error is raised.

        **Examples**

        ```prolog
        ?- X is sqrt(4).
        X = 2.0.

        ?- X is sqrt(2.25).
        X = 1.5.

        ?- X is sqrt(-1).
        throws error(evaluation_error(undefined), _).
        ```
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer.decimalValue, context)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real.value, context)

    /** Implements common behaviour for Integer and Real*/
    private fun commonBehaviour(
        decimal: BigDecimal,
        context: ExecutionContext,
    ): Real =
        if (decimal.signum < 0) {
            throwUndefinedError(context)
        } else {
            // always rounded: kt-math finds an inexact root only after computing it in full, then throws (gciatto/kt-math#553)
            Real.of(decimal.sqrt(context.flags.realMathContext))
        }
}
