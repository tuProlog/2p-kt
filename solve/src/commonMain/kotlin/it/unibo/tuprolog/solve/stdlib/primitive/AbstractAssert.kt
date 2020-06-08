package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class AbstractAssert(suffix: String, private val before: Boolean) : SideEffect1<ExecutionContext>("assert$suffix") {
    override fun accept(request: Solve.Request<ExecutionContext>, term: Term): Solve.Response =
        with(request.ensuringArgumentIsStruct(0)) {
            val clause = if (term is Clause) term else Fact.of(term as Struct)
            replySuccess(
                dynamicKb = context.dynamicKb.let {
                    if (before) {
                        it.assertA(clause)
                    } else {
                        it.assertZ(clause)
                    }
                }
            )
        }

}