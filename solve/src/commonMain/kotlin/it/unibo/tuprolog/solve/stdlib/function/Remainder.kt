package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.IntegersBinaryMathFunction
import org.gciatto.kt.math.BigInteger

/**
 * Implementation of `rem/2` arithmetic functor
 *
 * @author Enrico
 */
object Remainder : IntegersBinaryMathFunction("rem") {
    override val help: String =
        """
        `rem(+Dividend, +Divisor)`
        
        Evaluates the integer remainder of `Dividend` divided by `Divisor`. Both operands must evaluate to integers. A zero divisor raises an evaluation error. On the JVM the result takes the sign of `Dividend` (e.g. `-7 rem 2` is `-1`); on JavaScript the current implementation returns a non-negative result (`-7 rem 2` is `1`) and fails with a host arithmetic exception for a negative `Divisor`.

        **Examples**

        ```prolog
        ?- X is 7 rem 2.
        X = 1.

        ?- X is 7 rem 0.
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
            else -> Numeric.of(integer1.value.rem(integer2.value))
        }
}
