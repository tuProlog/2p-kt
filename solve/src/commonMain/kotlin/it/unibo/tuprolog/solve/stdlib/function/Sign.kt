package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction

/**
 * Implementation of `sign/1` arithmetic functor
 *
 * @author Enrico
 */
object Sign : UnaryMathFunction("sign") {
    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = Integer.of(integer.value.signum.takeIf { it != 0 } ?: 1)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric =
        Real.of(
            real.value.signum
                .takeIf { it != 0 }
                ?.toDouble() ?: 1.0,
        )
}
