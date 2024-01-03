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
