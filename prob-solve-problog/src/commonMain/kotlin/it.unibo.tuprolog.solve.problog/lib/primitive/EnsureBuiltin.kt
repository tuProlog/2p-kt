package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib

object EnsureBuiltin : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}EnsureBuiltin"
) {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsStruct(0)
        val signature = (first as Struct).extractSignature()
        return replyWith(first is Truth || signature in context.libraries)
    }
}