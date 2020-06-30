package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.utils.buffered

object Retract : UnaryPredicate<ExecutionContext>("retract") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsStruct(0)
        val clause = if (first is Clause) first else Rule.of(first as Struct, Var.anonymous())
        return context.dynamicKb[clause].buffered().map {
            val substitution = when (first) {
                is Clause -> (first mguWith it) as Substitution.Unifier
                else -> (first mguWith it.head!!) as Substitution.Unifier
            }
            replySuccess(substitution) {
                removeDynamicClauses(it)
            }
        }
    }
}