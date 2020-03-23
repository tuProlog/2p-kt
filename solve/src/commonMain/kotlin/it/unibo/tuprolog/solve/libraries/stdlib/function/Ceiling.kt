package it.unibo.tuprolog.solve.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.math.ceil

/**
 * Implementation of `ceiling/1` arithmetic functor
 *
 * @author Enrico
 */
object Ceiling : UnaryMathFunction("ceiling") {

    override fun mathFunction(integer: Integer, context: ExecutionContext): Numeric = integer

    override fun mathFunction(real: Real, context: ExecutionContext): Numeric =
        Numeric.of(ceil(real.value.toDouble()).toLong())
}
