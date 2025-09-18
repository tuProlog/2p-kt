package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

abstract class AbstractSetData(
    suffix: String,
) : BinaryRelation.NonBacktrackable<ExecutionContext>("set_$suffix") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        ensuringArgumentIsGround(0)
        val key = (first as Atom).value
        return replySuccess {
            setData(key, second)
        }
    }

    protected abstract fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    )
}
