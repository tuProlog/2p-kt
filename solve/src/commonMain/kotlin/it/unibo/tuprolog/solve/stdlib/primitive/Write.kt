package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

object Write : SideEffect1<ExecutionContext>("write") {
    override fun accept(request: Solve.Request<ExecutionContext>, term: Term): Solve.Response {
        return request.context.standardOutput.let {
            if (it == null) {
                request.replyFail()
            } else {
                val string = when (term) {
                    is Atom -> term.value
                    else -> term.toString()
                }
                it.write(string)
                request.replySuccess()
            }
        }
    }
}