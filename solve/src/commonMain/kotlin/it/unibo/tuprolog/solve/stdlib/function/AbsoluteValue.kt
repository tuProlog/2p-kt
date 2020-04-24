package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Implementation of `abs/1` arithmetic functor
 *
 * @author Enrico
 */
object AbsoluteValue : UnaryMathFunction("abs") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        Numeric.of(integer.value.absoluteValue) // TODO: 24/10/2019 missing Prolog Standard "int_overflow" check (see the standard)

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        Numeric.of(real.value.absoluteValue)

}
