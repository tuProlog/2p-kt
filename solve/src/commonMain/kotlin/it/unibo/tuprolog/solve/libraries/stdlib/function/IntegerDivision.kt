package it.unibo.tuprolog.solve.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigInteger

/**
 * Implementation of `'//'/2` arithmetic functor
 *
 * @author Enrico
 */
object IntegerDivision : IntegersBinaryMathFunction("//") {

    override fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric =
        // TODO: 25/10/2019 "int_overflow" checks missing (see the standard)
        when (integer2.value) {
            BigInteger.ZERO -> throwZeroDivisorError(context)
            else -> Numeric.of(integer1.value / integer2.value)
        }
}
