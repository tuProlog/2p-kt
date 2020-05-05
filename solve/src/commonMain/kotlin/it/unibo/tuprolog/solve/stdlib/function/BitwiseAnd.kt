package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Implementation of `'/\'/2` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseAnd : IntegersBinaryMathFunction("/\\") {

    override fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric =
        Numeric.of(integer1.value.and(integer2.value))
}