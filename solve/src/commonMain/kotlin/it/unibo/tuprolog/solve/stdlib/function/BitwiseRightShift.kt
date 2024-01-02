package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.IntegersBinaryMathFunction

/**
 * Implementation of `'>>'/2` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseRightShift : IntegersBinaryMathFunction(">>") {
    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric = Numeric.of(integer1.value.shr(integer2.value.toInt()))
}
