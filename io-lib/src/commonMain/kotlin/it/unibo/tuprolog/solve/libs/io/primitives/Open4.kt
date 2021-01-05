package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object Open4 : QuaternaryRelation.NonBacktrackable<ExecutionContext>("open") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term
    ): Solve.Response {
        return notSupported()
    }
}
