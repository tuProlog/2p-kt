package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of `'\=='/2` predicate */
object TermNotIdentical : BinaryRelation.Predicative<ExecutionContext>("\\==") {
    override val help: String =
        """
        `Left \== Right`
        
        Succeeds when `Left` and `Right` are not identical terms. It is the negation of term identity and does not bind variables.

        **Examples**

        ```prolog
        ?- a \== b.
        yes.

        ?- X \== Y.
        yes.

        ?- X \== X.
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first != second
}
