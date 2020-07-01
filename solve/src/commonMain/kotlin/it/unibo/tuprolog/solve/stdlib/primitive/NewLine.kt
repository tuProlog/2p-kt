package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.PredicateWithoutArguments

object NewLine : PredicateWithoutArguments.NonBacktrackable<ExecutionContext>("nl") {
    override fun Solve.Request<ExecutionContext>.computeOne(): Solve.Response {
        return context.standardOutput.let {
            if (it == null) {
                replyFail()
            } else {
                it.write("\n")
                replySuccess()
            }
        }
    }
}