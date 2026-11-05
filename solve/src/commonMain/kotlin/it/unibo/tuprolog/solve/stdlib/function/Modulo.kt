package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.IntegersBinaryMathFunction
import org.gciatto.kt.math.BigInteger

/**
 * Implementation of `mod/2` arithmetic functor
 *
 * @author Enrico
 */
object Modulo : IntegersBinaryMathFunction("mod") {
    override val help: String =
        """
        `mod(+Dividend, +Divisor)`
        
        Evaluates `Dividend - floor(Dividend / Divisor) * Divisor`, as ISO prescribes: unlike `rem`, the result takes the sign of `Divisor` (e.g. `-7 mod 2` is `1` and `7 mod -2` is `-1`). Both operands must evaluate to integers. A zero divisor raises an evaluation error.

        **Examples**

        ```prolog
        ?- X is 7 mod 2.
        X = 1.

        ?- X is -7 mod 2.
        X = 1.

        ?- X is 7 mod 0.
        throws error(evaluation_error(zero_divisor), _).
        ```
        """.trimIndent()

    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric =
        when (integer2.value) {
            BigInteger.ZERO -> throwZeroDivisorError(context)
            else -> Numeric.of(floorMod(integer1.value, integer2.value))
        }

    private fun floorMod(
        dividend: BigInteger,
        divisor: BigInteger,
    ): BigInteger {
        val remainder = dividend.remainder(divisor)
        return if (remainder.signum != 0 && remainder.signum != divisor.signum) remainder + divisor else remainder
    }
}
