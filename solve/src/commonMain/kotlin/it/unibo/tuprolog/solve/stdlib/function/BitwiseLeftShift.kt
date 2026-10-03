package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.IntegersBinaryMathFunction

/**
 * Implementation of `'<<'/2` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseLeftShift : IntegersBinaryMathFunction("<<") {
    override val help: String =
        """
        `<<(+Integer, +Shift)`
        
        Shifts `Integer` left by `Shift` bit positions. Both operands must evaluate to integers.

        **Examples**

        ```prolog
        ?- X is 1 << 4.
        X = 16.

        ?- X is 1.0 << 4.
        throws error(type_error(integer, 1.0), _).
        ```
        """.trimIndent()

    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric = Numeric.of(integer1.value.shl(integer2.value.toInt()))
}
