package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement unary math functions
 *
 * @param name the function name
 *
 * @author Enrico
 */
abstract class UnaryMathFunction(
    name: String,
) : MathFunction(name, 1) {
    override fun uncheckedImplementation(request: Compute.Request<ExecutionContext>): Compute.Response =
        with(request) {
            val term = arguments.single()
            replyWith(
                when (term) {
                    is Integer -> mathFunction(term, context)
                    is Real -> mathFunction(term, context)
                    else -> term
                },
            )
        }

    /** The actual math function implementation, for [Integer]s */
    protected abstract fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric

    /** The actual math function implementation, for [Real]s */
    protected abstract fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric
}
