package it.unibo.tuprolog.solve.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext

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
