package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

abstract class AbstractAssert(
    suffix: String,
    private val before: Boolean
) : UnaryPredicate.NonBacktrackable<ExecutionContext>("assert$suffix") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsStruct(0)
        val clause = if (first is Clause) first else Fact.of(first as Struct)
        return replySuccess(
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