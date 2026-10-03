package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction

/**
 * Implementation of `abs/1` arithmetic functor
 *
 * @author Enrico
 */
object AbsoluteValue : UnaryMathFunction("abs") {
    override val help: String =
        """
        `abs(+Number)`
        
        Evaluates to the absolute value of `Number`. Integer inputs produce integers; real inputs produce reals.

        **Examples**

        ```prolog
        ?- X is abs(-3).
        X = 3.

        ?- X is abs(-2.5).
        X = 2.5.

        ?- X is abs(foo).
        throws error(type_error(evaluable, foo), _).
        ```
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric =
        Numeric.of(
            integer.value.absoluteValue,
        ) // TODO: 24/10/2019 missing Prolog Standard "int_overflow" check (see the standard)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = Numeric.of(real.value.absoluteValue)
}
