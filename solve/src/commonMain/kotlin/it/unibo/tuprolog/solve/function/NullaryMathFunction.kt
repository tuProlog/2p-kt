package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement nullary math functions (with no arguments)
 *
 * @param name the no arguments function name
 *
 * @author Enrico
 */
abstract class NullaryMathFunction(
    name: String,
) : MathFunction(name, 0) {
    override fun uncheckedImplementation(request: Compute.Request<ExecutionContext>): Compute.Response =
        with(request) { replyWith(mathFunction(context)) }

    /** The actual function implementation */
    protected abstract fun mathFunction(context: ExecutionContext): Numeric
}
