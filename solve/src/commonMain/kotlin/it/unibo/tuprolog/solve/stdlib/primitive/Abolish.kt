package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.PermissionError.Operation.MODIFY
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Abolish : UnaryPredicate.NonBacktrackable<ExecutionContext>("abolish") {
    override val help: String =
        """
        `abolish(+PredicateIndicator)`
        
        Removes all clauses of the dynamic predicate identified by `PredicateIndicator` (for example `foo/2`). The indicator must be well formed and the predicate must be modifiable.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsWellFormedIndicator(0)
        val indicator = first as Indicator
        val clausesToBeRemoved = context.dynamicKb[indicator]
        ensuringProcedureHasPermission(Signature.fromIndicator(indicator), MODIFY)
        return replySuccess {
            removeDynamicClauses(clausesToBeRemoved)
        }
    }
}
