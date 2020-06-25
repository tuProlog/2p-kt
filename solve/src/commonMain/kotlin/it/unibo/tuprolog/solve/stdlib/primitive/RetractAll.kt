package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object RetractAll : UnaryPredicate.NonBacktrackable<ExecutionContext>("retractall") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsStruct(0)
        val clause = if (first is Clause) first else Rule.of(first as Struct, Var.anonymous())
        val dynamicKb = context.dynamicKb
        val result = dynamicKb.retractAll(clause)
        return replySuccess(dynamicKb = result.theory)
    }
}