package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '\='/2 predicate */
object NotUnifiableWith : BinaryRelation.Predicative<ExecutionContext>("\\=") {
    override val help: String =
        """
        `Left \= Right`
        
        Succeeds when `Left` and `Right` do not unify; no bindings are made. Fails when a unifier exists.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = !match(first, second)
}
