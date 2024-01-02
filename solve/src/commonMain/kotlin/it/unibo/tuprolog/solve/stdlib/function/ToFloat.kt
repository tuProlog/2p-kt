package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction

/**
 * Implementation of `float/1` arithmetic functor
 *
 * @author Enrico
 */
object ToFloat : UnaryMathFunction("float") {
    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(integer)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = commonBehaviour(real)

    /** Implementation of common behaviour for Integer and Real */
    private fun commonBehaviour(numeric: Numeric) = Numeric.of(numeric.decimalValue)
}
