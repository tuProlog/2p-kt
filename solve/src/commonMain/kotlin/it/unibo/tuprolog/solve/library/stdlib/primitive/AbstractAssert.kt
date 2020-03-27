package it.unibo.tuprolog.solve.library.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class AbstractAssert(suffix: String, private val before: Boolean) : SideEffect1<ExecutionContext>("assert$suffix") {
    override fun accept(request: Solve.Request<ExecutionContext>, term: Term): Solve.Response =
        with(request.ensuringArgumentIsStruct(0)) {
            replySuccess(
                dynamicKB = context.dynamicKb.let {
                    if (before) {
                        it.assertA(term as Struct)
                    } else {
                        it.assertZ(term as Struct)
                    }
                }
            )
        }

}