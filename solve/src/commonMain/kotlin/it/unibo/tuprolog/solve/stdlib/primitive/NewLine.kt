package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitive.SideEffect0

object NewLine : SideEffect0<ExecutionContext>("nl") {
    override fun accept(request: Solve.Request<ExecutionContext>): Solve.Response {
        return request.context.standardOutput.let {
            if (it == null) {
                request.replyFail()
            } else {
                it.write("\n")
                request.replySuccess()
            }
        }
    }
}