package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction

/**
 * Implementation of `'\'/1` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseComplement : UnaryMathFunction("\\") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric =
        Numeric.of(integer.value.not())

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        throwTypeErrorBecauseOnlyIntegersAccepted(functor, real, context)
}
