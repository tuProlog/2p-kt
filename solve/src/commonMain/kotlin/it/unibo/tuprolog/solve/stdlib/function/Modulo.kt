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
        
        Evaluates the truncated integer remainder of `Dividend` divided by `Divisor`: the current implementation gives the result the sign of `Dividend` (e.g. `-7 mod 2` is `-1` and `7 mod -2` is `1`), rather than the sign of `Divisor`. Both operands must evaluate to integers. A zero divisor raises an evaluation error.
        """.trimIndent()

    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric =
        when (integer2.value) {
            BigInteger.ZERO -> throwZeroDivisorError(context)
            else -> Numeric.of(integer1.value.remainder(integer2.value))
        }
}
