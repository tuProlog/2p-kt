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
    override val help: String =
        """
        `float(+Number)`
        
        Evaluates to `Number` converted to a real (floating-point) value; real inputs are returned unchanged.

        **Examples**

        ```prolog
        ?- X is float(3).
        X = 3.0.

        ?- X is float(2.5).
        X = 2.5.

        ?- X is float(foo).
        throws error(type_error(evaluable, foo), _).
        ```
        """.trimIndent()

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
