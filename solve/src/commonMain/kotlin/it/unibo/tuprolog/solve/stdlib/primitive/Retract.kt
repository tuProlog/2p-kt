package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object Retract: BacktrackableSideEffect1<ExecutionContext>("retract") {
    override fun accept(request: Solve.Request<ExecutionContext>, term: Term): Sequence<Solve.Response> {
        request.ensuringArgumentIsStruct(0)
        val clause = if (term is Clause) term else Rule.of(term as Struct, Var.anonymous())
        return sequence {
            var dynamicKb = request.context.dynamicKb
            while (true) {
                val result = dynamicKb.retract(clause)
                if (result is RetractResult.Success) {
                    dynamicKb = result.theory
                    val substitution = when (term) {
                        is Clause -> (term mguWith result.firstClause) as Substitution.Unifier
                        else -> (term mguWith result.firstClause.head!!) as Substitution.Unifier
                    }
                    yield(
                        request.replySuccess(substitution = substitution, dynamicKb = dynamicKb)
                    )
                } else {
                    break
                }
            }
        }
    }

}