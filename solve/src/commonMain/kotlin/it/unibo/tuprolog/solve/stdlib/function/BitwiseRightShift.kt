package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.IntegersBinaryMathFunction

/**
 * Implementation of `'>>'/2` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseRightShift : IntegersBinaryMathFunction(">>") {
    override val help: String =
        """
        `>>(+Integer, +Shift)`
        
        Shifts `Integer` right by `Shift` bit positions. Both operands must evaluate to integers.

        **Examples**

        ```prolog
        ?- X is 16 >> 2.
        X = 4.

        ?- X is -16 >> 2.
        X = -4.
        ```
        """.trimIndent()

    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric = Numeric.of(integer1.value.shr(integer2.value.toInt()))
}
