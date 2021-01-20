package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm

/**
 * This exists only for debug purposes. Formats an [ProbExplanationTerm] to a graph-like structure
 * using Graphviz notation and prints the result to the output channel.
 *
 * @author Jason Dellaluce
 */
internal object ProbExplDebug : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_expl_debug"
) {
    private const val debugEnabled = false

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        if (!debugEnabled) {
            return replySuccess()
        } else if (first !is ProbExplanationTerm) {
            return replyFail()
        }
        return context.outputChannels.current.let {
            if (it == null) {
                replyFail()
            } else {
                it.write(first.explanation.formatToGraphviz() + "\n")
                replySuccess()
            }
        }
    }
}
