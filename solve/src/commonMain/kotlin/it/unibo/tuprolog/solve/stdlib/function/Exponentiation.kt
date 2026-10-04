package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.BinaryMathFunction
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.math.pow

/**
 * Implementation of `'**'/2` arithmetic functor
 *
 * @author Enrico
 */
object Exponentiation : BinaryMathFunction("**") {
    override val help: String =
        """
        `**(+Base, +Exponent)`
        
        Evaluates `Base` raised to `Exponent`. The result is represented as a real value; the current implementation defines `0 ** 0` as `1.0`. Raising zero to a negative exponent, or a negative base to a fractional one, raises the `undefined` evaluation error; results too large to be represented raise the `float_overflow` one.

        **Examples**

        ```prolog
        ?- X is 2 ** 3.
        X = 8.0.

        ?- X is 4 ** 0.5.
        X = 2.0.

        ?- X is 0 ** 0.
        X = 1.0.

        ?- X is 2 ** -1.
        X = 0.5.

        ?- X is 0 ** -1.
        throws error(evaluation_error(undefined), _).
        ```
        """.trimIndent()

    // TODO: 24/10/2019 missing "underflow" error check (see the standard)

    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric = power(integer1.decimalValue, integer2, context)

    override fun mathFunction(
        real: Real,
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = power(real.value, integer, context)

    override fun mathFunction(
        integer: Integer,
        real: Real,
        context: ExecutionContext,
    ): Numeric = power(integer.decimalValue, real.value, context)

    override fun mathFunction(
        real1: Real,
        real2: Real,
        context: ExecutionContext,
    ): Numeric = power(real1.value, real2.value, context)

    /** Exact for natural exponents [BigDecimal.pow] supports, floating-point otherwise. */
    private fun power(
        base: BigDecimal,
        exponent: Integer,
        context: ExecutionContext,
    ): Real =
        if (exponent.value.signum >= 0 && exponent.value <= BigInteger.of(MAX_EXACT_EXPONENT)) {
            Real.of(base.pow(exponent.value.toInt()))
        } else {
            power(base, exponent.decimalValue, context)
        }

    private fun power(
        base: BigDecimal,
        exponent: BigDecimal,
        context: ExecutionContext,
    ): Real =
        if (base.signum == 0 && exponent.signum < 0) {
            throwUndefinedError(context)
        } else {
            realOf(base.toDouble().pow(exponent.toDouble()), context)
        }

    private const val MAX_EXACT_EXPONENT = 999_999_999
}
