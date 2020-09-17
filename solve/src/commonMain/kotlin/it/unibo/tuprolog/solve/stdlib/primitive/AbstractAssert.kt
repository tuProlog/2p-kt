package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.PermissionError.Operation.MODIFY
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

abstract class AbstractAssert(
    suffix: String,
    private val before: Boolean
) : UnaryPredicate.NonBacktrackable<ExecutionContext>("assert$suffix") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsWellFormedClause(0)
        val clause: Clause = when (first) {
            is Clause -> first
            is Struct -> Fact.of(first)
            else -> return ensuringArgumentIsCallable(0).replyFail()
        }
        ensuringClauseProcedureHasPermission(clause, MODIFY)
        return replySuccess {
            addDynamicClauses(clause.prepareForExecution(), onTop = before)
        }
    }
}
