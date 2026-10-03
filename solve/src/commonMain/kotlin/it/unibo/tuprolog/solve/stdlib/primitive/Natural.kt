package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.gciatto.kt.math.BigInteger

object Natural : UnaryPredicate<ExecutionContext>("natural") {
    override val help: String =
        """
        `natural(?N)`
        
        Recognizes non-negative integers. With a variable argument, it generates the infinite sequence `0, 1, 2, ...` on backtracking.

        **Examples**

        ```prolog
        ?- natural(N).
        N = 0 ; N = 1 ; N = 2.

        ?- natural(5).
        yes.

        ?- natural(-1).
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> =
        when (first) {
            is Var -> generateValues().map { replySuccess(Substitution.of(first, it)) }
            is Integer -> sequenceOf(replyWith(checkValue(first)))
            else -> sequenceOf(replyFail())
        }

    private fun generateValues(): Sequence<Term> =
        generateSequence(BigInteger.ZERO) {
            it + BigInteger.ONE
        }.map { Integer.of(it) }

    private fun checkValue(value: Integer): Boolean = value.intValue.signum >= 0
}
