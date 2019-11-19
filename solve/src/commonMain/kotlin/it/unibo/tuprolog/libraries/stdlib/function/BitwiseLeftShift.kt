package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Implementation of `'<<'/2` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseLeftShift : IntegersBinaryMathFunction("<<") {

    override fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric =
        Numeric.of(integer1.value.shl(integer2.value.toInt()))

}
