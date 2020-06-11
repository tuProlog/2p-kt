package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object Retract : UnaryPredicate<ExecutionContext>("retract") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsStruct(0)
        val clause = if (first is Clause) first else Rule.of(first as Struct, Var.anonymous())
        return sequence {
            var dynamicKb = context.dynamicKb
            while (true) {
                val result = dynamicKb.retract(clause)
                if (result is RetractResult.Success) {
                    dynamicKb = result.theory
                    val substitution = when (first) {
                        is Clause -> (first mguWith result.firstClause) as Substitution.Unifier
                        else -> (first mguWith result.firstClause.head!!) as Substitution.Unifier
                    }
                    yield(
                        replySuccess(substitution = substitution, dynamicKb = dynamicKb)
                    )
                } else {
                    break
                }
            }
        }
    }

}