package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.PermissionError.Operation.MODIFY
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.PRIVATE_PROCEDURE
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.STATIC_PROCEDURE
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Abolish : UnaryPredicate.NonBacktrackable<ExecutionContext>("abolish") {
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