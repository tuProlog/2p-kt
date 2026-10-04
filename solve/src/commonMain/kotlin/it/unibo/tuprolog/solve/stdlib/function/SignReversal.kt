package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction

/**
 * Implementation of `'-'/1` arithmetic functor
 *
 * @author Enrico
 */
object SignReversal : UnaryMathFunction("-") {
    override val help: String =
        """
        `-(+Number)`
        
        Evaluates to the arithmetic negation of `Number`.

        **Examples**

        ```prolog
        ?- Y = 3, X is -Y.
        X = -3.

        ?- X is -(-2.5).
        X = 2.5.
        ```
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric =
        // TODO: 25/10/2019 "int_overflow" check missing (see the standard)
        Integer.of(-integer.value)

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = Real.of(-real.value)
}
