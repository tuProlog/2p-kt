package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.open
import it.unibo.tuprolog.solve.primitive.Solve.Request
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.primitive.TernaryRelation

object Open3 : TernaryRelation.NonBacktrackable<ExecutionContext>("open") {
    override fun Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Response = open(third)
}
