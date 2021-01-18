package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

internal object ProbExplAnd : TernaryRelation.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_expl_and"
) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        return if (first is Var && second is ProbExplanationTerm && third is ProbExplanationTerm) {
            replyWith(first mguWith ProbExplanationTerm(second.explanation and third.explanation))
        } else replyException(TuPrologRuntimeException("Can't compute $functor", context = context))
    }
}
