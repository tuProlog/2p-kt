package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.PermissionError.Operation.MODIFY
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.PRIVATE_PROCEDURE
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission.STATIC_PROCEDURE
import it.unibo.tuprolog.solve.extractSignature
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
        val headSignature = clause.head?.extractSignature()
        if (headSignature != null) {
            if (context.libraries.hasProtected(headSignature)) {
                throw PermissionError.of(context, signature, MODIFY, PRIVATE_PROCEDURE, headSignature.toIndicator())
            }
            if (headSignature in context.staticKb) {
                throw PermissionError.of(context, signature, MODIFY, STATIC_PROCEDURE, headSignature.toIndicator())
            }
        }
        return replySuccess {
            addDynamicClauses(clause.prepareForExecution(), onTop = before)
        }
    }
}