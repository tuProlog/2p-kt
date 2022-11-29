package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.toTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.isPrologMode

/**
 * This applies an "And" logical operation between two explanations represented by [ProbExplanationTerm].
 *
 * @author Jason Dellaluce
 */
internal object ProbExplAnd : TernaryRelation.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_expl_and"
) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)

        /* Skip computations for Prolog-only queries */
        if (context.isPrologMode()) {
            return replyWith(mgu(first, ProbExplanation.TRUE.toTerm()))
        }

        return if (first is Var && second is ProbExplanationTerm && third is ProbExplanationTerm) {
            val explanation = when {
                second.explanation.probability == 0.0 || third.explanation.probability == 0.0 -> ProbExplanation.FALSE
                second.explanation.probability == 1.0 && third.explanation.probability == 1.0 -> ProbExplanation.TRUE
                second.explanation.probability == 1.0 -> third.explanation
                third.explanation.probability == 1.0 -> second.explanation
                else -> second.explanation and third.explanation
            }
            if (explanation.probability == 0.0) {
                replyWith(false)
            } else {
                replyWith(mgu(first, ProbExplanationTerm(explanation)))
            }
        } else replyException(ResolutionException("Can't compute $functor", context = context))
    }
}
