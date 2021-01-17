package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.bdd.toGraphvizString
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef

object ProbDebugSolution : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_debug_sol") {
    private const val debugEnabled = true

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        if (!debugEnabled) {
            return replySuccess()
        } else if (first !is ProblogObjectRef) {
            return replyFail()
        }
        return context.standardOutput.let {
            if (it == null) {
                replyFail()
            } else {
                it.write(first.bdd.toGraphvizString() + "\n")
                replySuccess()
            }
        }
    }
}
