package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.UnaryMathFunction

/**
 * Implementation of `'\'/1` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseComplement : UnaryMathFunction("\\") {
    override val help: String =
        """
        `\(+Integer)`
        
        Evaluates to the bitwise complement of `Integer`. The operand must evaluate to an integer.

        **Examples**

        ```prolog
        ?- X is \(5).
        X = -6.

        ?- X is \(2.5).
        throws error(type_error(integer, 2.5), _).
        ```
        """.trimIndent()

    override fun mathFunction(
        integer: Integer,
        context: ExecutionContext,
    ): Numeric = Numeric.of(integer.value.not())

    override fun mathFunction(
        real: Real,
        context: ExecutionContext,
    ): Numeric = throwTypeErrorBecauseOnlyIntegersAccepted(functor, real, context)
}
