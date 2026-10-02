package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '=='/2 predicate */
object TermNotSame : BinaryRelation.Predicative<ExecutionContext>("\\=@=") {
    override val help: String =
        """
        `Left \=@= Right`
        
        Succeeds when `Left` and `Right` are not structurally the same up to consistent variable renaming.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first.compareTo(second) != 0
}
