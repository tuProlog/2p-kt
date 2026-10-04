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
        
        Evaluates to the natural (base `e`) logarithm of `Number`, always as a real value. `Number` must be positive, otherwise the `undefined` evaluation error is raised.

        **Examples**

        ```prolog
        ?- X is log(1).
        X = 0.0.

        ?- X is log(exp(2)), X > 1.999, X < 2.001.
        yes.

        ?- X is log(0).
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

    /** Implementation of common behaviour for Integer and Real */
    private fun commonBehaviour(
        decimal: BigDecimal,
        context: ExecutionContext,
    ): Real =
        if (decimal.signum <= 0) {
            throwUndefinedError(context)
        } else {
            realOf(log(decimal.toDouble(), E), context)
        }
}
