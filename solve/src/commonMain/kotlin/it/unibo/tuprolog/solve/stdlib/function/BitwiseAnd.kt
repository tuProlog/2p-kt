package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.function.IntegersBinaryMathFunction

/**
 * Implementation of `'/\'/2` arithmetic functor
 *
 * @author Enrico
 */
object BitwiseAnd : IntegersBinaryMathFunction("/\\") {
    override val help: String =
        """
        `/\(+Left, +Right)`
        
        Evaluates to the bitwise AND of the two integer operands. Both operands must evaluate to integers.
        """.trimIndent()

    override fun mathFunction(
        integer1: Integer,
        integer2: Integer,
        context: ExecutionContext,
    ): Numeric = Numeric.of(integer1.value.and(integer2.value))
}
