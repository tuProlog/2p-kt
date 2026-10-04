package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '='/2 predicate */
object UnifiesWith : BinaryRelation.Functional<ExecutionContext>("=") {
    override val help: String =
        """
        `Left = Right`
        
        Unifies `Left` and `Right`, succeeding with their most general unifier when one exists and failing otherwise.

        **Examples**

        ```prolog
        ?- f(X, b) = f(a, Y).
        X = a, Y = b.

        ?- X = Y, Y = 1.
        X = 1.

        ?- f(X) = g(X).
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution = mgu(first, second)
}
