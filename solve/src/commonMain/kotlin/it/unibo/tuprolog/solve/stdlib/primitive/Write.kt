package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Write : UnaryPredicate.NonBacktrackable<ExecutionContext>("write") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        return context.standardOutput.let {
            if (it == null) {
                replyFail()
            } else {
                val string = when (first) {
                    is Atom -> first.value
                    else -> first.toString()
                }
                it.write(string)
                replySuccess()
            }
        }
    }
}