package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '=='/2 predicate */
object TermIdentical : BinaryRelation.Predicative<ExecutionContext>("==") {
    override val help: String =
        """
        `Left == Right`
        
        Succeeds when `Left` and `Right` are identical terms without performing unification; variables must denote the same variables in the same positions.

        **Examples**

        ```prolog
        ?- f(a, X) == f(a, X).
        yes.

        ?- X == Y.
        no.

        ?- 1 == 1.0.
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first == second
}
