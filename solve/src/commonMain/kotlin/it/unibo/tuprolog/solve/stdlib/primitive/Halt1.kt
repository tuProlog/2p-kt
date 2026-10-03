package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Halt1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("halt") {
    override val help: String =
        """
        `halt(+Status)`
        
        Stops the current Prolog computation with the non-negative integer exit status `Status`.

        **Examples**

        ```prolog
        % stops the computation with exit status 1
        ?- halt(1).
        yes.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsNonNegativeInteger(0)
        throw HaltException(context = context, exitStatus = (first.castToInteger()).intValue.toInt())
    }
}
