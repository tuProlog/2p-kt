package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

abstract class AbstractAssert(
    suffix: String,
    private val before: Boolean
) : UnaryPredicate.NonBacktrackable<ExecutionContext>("assert$suffix") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsStruct(0)
        val clause = if (first is Clause) first else Fact.of(first as Struct)
        return replySuccess {
            addDynamicClauses(clause, onTop = before)
        }
    }
}