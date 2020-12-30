package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.bdd.expansion
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object ProbCalc : BinaryRelation.NonBacktrackable<ExecutionContext>("${ProblogLib.PREDICATE_PREFIX}_calc") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        return if (second is ProblogObjectRef) {
            val probability = second.bdd.expansion(0.0, 1.0) {
                node, low, high ->
                node.probability * high + (1.0 - node.probability) * low
            }
            replyWith(first mguWith Numeric.of(probability))
        } else {
            replyException(
                TuPrologRuntimeException("Can't compute probability", context = context)
            )
        }
    }
}
