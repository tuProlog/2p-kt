package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '=='/2 predicate */
object TermSame : BinaryRelation.Predicative<ExecutionContext>("=@=") {
    override val help: String =
        """
        `Left =@= Right`
        
        Succeeds when `Left` and `Right` have the same structure up to consistent renaming of variables. It does not instantiate either term.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first.compareTo(second) == 0
}
