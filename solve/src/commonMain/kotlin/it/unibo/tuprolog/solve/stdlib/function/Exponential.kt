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
        
        Evaluates to `e` (Euler's number) raised to `Number`, always as a real value. Results too large to be represented, e.g. `exp(1000)`, raise the `float_overflow` evaluation error.

        **Examples**

        ```prolog
        ?- X is exp(0).
        X = 1.0.

        ?- X is exp(1), X > 2.718, X < 2.719.
        yes.

        ?- X is exp(1000).
        throws error(evaluation_error(float_overflow), _).
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

    // TODO: 24/10/2019 missing "underflow" check (see the standard)

    /** Implements the common behaviour for real and integer */
    private fun commonBehaviour(
        decimal: BigDecimal,
        context: ExecutionContext,
    ) = realOf(exp(decimal.toDouble()), context)
}
