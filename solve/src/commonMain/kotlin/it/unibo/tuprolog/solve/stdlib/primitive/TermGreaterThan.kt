package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '@>'/2 predicate */
object TermGreaterThan : BinaryRelation.Predicative<ExecutionContext>("@>") {
    override val help: String =
        """
        `Left @> Right`
        
        Succeeds when `Left` follows `Right` in the standard term ordering used by the solver. No arithmetic evaluation or unification is performed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first > second
}
