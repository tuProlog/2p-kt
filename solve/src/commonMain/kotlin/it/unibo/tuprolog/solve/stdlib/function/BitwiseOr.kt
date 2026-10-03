package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.IntegersBinaryMathFunction

/**
 * Implementation of `'\/'/2` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseOr : IntegersBinaryMathFunction("\\/") {
    override val help: String =
        """
        `\/(+Left, +Right)`
        
        Evaluates to the bitwise OR of the two integer operands. Both operands must evaluate to integers.

        **Examples**

        ```prolog
        ?- X is 12 \/ 10.
        X = 14.

        ?- X is 12 \/ 1.5.
        throws error(type_error(integer, 1.5), _).
        ```
        """.trimIndent()

    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric = Numeric.of(integer1.value.or(integer2.value))
}
