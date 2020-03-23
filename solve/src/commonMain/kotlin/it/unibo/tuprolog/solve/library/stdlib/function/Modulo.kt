package it.unibo.tuprolog.solve.library.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigInteger

/**
 * Implementation of `mod/2` arithmetic functor
 *
 * @author Enrico
 */
object Modulo : IntegersBinaryMathFunction("mod") {

    override fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric =
        when (integer2.value) {
            BigInteger.ZERO -> throwZeroDivisorError(context)
            else -> Numeric.of(integer1.value.remainder(integer2.value))
        }
}
