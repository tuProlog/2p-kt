package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.bdd.and
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object ProbBuildAnd : TernaryRelation.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_dd_and"
) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        return if (first is Var && second is ProblogObjectRef && third is ProblogObjectRef) {
            replyWith(first mguWith ProblogObjectRef(second.bdd and third.bdd))
        } else replyException(TuPrologRuntimeException("Can't compute $functor", context = context))
    }
}
