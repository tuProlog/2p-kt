package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class to implement math functions accepting only integers
 *
 * @param name the function name
 *
 * @author Enrico
 */
abstract class IntegersBinaryMathFunction(
    name: String,
) : BinaryMathFunction(name) {
    override fun mathFunction(
        real: Real,
        integer: Integer,
        context: ExecutionContext,
    ): Numeric =
        throwTypeErrorBecauseOnlyIntegersAccepted(
            functor,
            real,
            context,
        )

    override fun mathFunction(
        integer: Integer,
        real: Real,
        context: ExecutionContext,
    ): Numeric =
        throwTypeErrorBecauseOnlyIntegersAccepted(
            functor,
            real,
            context,
        )

    override fun mathFunction(
        real1: Real,
        real2: Real,
        context: ExecutionContext,
    ): Numeric =
        throwTypeErrorBecauseOnlyIntegersAccepted(
            functor,
            real1,
            context,
        )
}
