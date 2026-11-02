package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '\=@='/2 predicate */
object TermNotSame : BinaryRelation.Predicative<ExecutionContext>("\\=@=") {
    override val help: String =
        """
        `Left \=@= Right`
        
        Succeeds when `Left` and `Right` are not variants of each other (see `=@=/2`).

        **Examples**

        ```prolog
        ?- f(a) \=@= f(b).
        yes.

        ?- f(X) \=@= f(X).
        no.

        ?- f(X) \=@= f(Y).
        no.

        ?- f(X, X) \=@= f(Y, Z).
        yes.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = !(first isVariantOf second)
}
