package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Write : UnaryPredicate.NonBacktrackable<ExecutionContext>("write") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        return context.outputChannels.current.let {
            if (it == null) {
                replyFail()
            } else {
                val string = first.format(TermFormatter.default(context.operators))
                return try {
                    it.write(string)
                    replySuccess()
                } catch (_: IllegalStateException) {
                    replyFail()
                }
            }
        }
    }
}
