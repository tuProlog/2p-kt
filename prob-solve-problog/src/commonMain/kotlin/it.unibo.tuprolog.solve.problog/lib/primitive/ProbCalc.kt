package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/**
 * This primitive can be used to compute the probability of a [ProbExplanationTerm].
 * The first argument gets substituted with the computed probability, and the second
 * argument is the explanation provided in the form of [ProbExplanationTerm].
 *
 * @author Jason Dellaluce
 */
object ProbCalc : BinaryRelation.NonBacktrackable<ExecutionContext>("${ProblogLib.PREDICATE_PREFIX}_calc") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)

        return if (second is ProbExplanationTerm) {
            try {
                val probability = second.explanation.probability
                replyWith(first mguWith Numeric.of(probability))
            } catch (e: Throwable) {
                replyException(
                    TuPrologRuntimeException(
                        "Error during probability computation",
                        context = context,
                        cause = e
                    )
                )
            }
        } else {
            replyException(
                TuPrologRuntimeException("Can't compute probability", context = context)
            )
        }
    }
}
