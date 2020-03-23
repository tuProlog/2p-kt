package it.unibo.tuprolog.solve.library.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.function.Compute
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement unary math functions
 *
 * @param name the function name
 *
 * @author Enrico
 */
abstract class BinaryMathFunction(name: String) : MathFunction(name, 2) {

    override fun uncheckedImplementation(request: Compute.Request<ExecutionContext>): Compute.Response = with(request) {
        val term1 = arguments.first()
        val term2 = arguments.last()
        replyWith(
            when {
                term1 is Integer && term2 is Integer -> mathFunction(term1, term2, context)
                term1 is Real && term2 is Integer -> mathFunction(term1, term2, context)
                term1 is Integer && term2 is Real -> mathFunction(term1, term2, context)
                term1 is Real && term2 is Real -> mathFunction(term1, term2, context)
                else -> throw IllegalStateException("One, or both, of the two terms (`$term1`, `$term2`) is neither an Integer nor a Real")
            }
        )
    }

    /** The actual math function implementation, for [Integer]s */
    protected abstract fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric

    /** The actual math function implementation, for [Real] and [Integer] */
    protected abstract fun mathFunction(real: Real, integer: Integer, context: ExecutionContext): Numeric

    /** The actual math function implementation, for [Integer] and [Real] */
    protected abstract fun mathFunction(integer: Integer, real: Real, context: ExecutionContext): Numeric

    /** The actual math function implementation, for [Real]s */
    protected abstract fun mathFunction(real1: Real, real2: Real, context: ExecutionContext): Numeric
}
