package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.BinaryMathFunction
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.big.isLongValue
import org.gciatto.kt.math.big.pow
import kotlin.math.absoluteValue

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

    private fun power(
        base: BigDecimal,
        exponent: Integer,
        context: ExecutionContext,
    ): Real = power(base, exponent.decimalValue, context)

    private fun power(
        base: BigDecimal,
        exponent: BigDecimal,
        context: ExecutionContext,
    ): Real =
        if (base.signum == 0 && exponent.signum < 0) {
            throwUndefinedError(context)
        } else {
            computeReal(context) {
                when {
                    !exponent.isLongValue() -> base.pow(exponent, it)
                    it.precision == 0 && base.precision.toLong() * exponent.toLong().absoluteValue > MAX_EXACT_DIGITS ->
                        throw ArithmeticException("Too many digits for an exact power")
                    // big-math's pow with a BigDecimal exponent requires a limited precision, even for integral ones
                    else -> base.pow(exponent.toLong(), it)
                }
            }
        }

    // ponytail: exact powers are capped at this many (estimated) digits, beyond which they are rounded (or undefined,
    // with an unlimited real_precision_mode): computing them exactly would exhaust time and memory. Make it a flag if
    // anyone needs exact results this large.
    private const val MAX_EXACT_DIGITS = 100_000L
}
