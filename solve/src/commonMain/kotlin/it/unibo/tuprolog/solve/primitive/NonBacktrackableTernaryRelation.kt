package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/** Base class to implement primitives that relate tree [Term]s and are not back-trackable */
abstract class NonBacktrackableTernaryRelation<E : ExecutionContext>(operator: String) : TernaryRelation<E>(operator) {

    override fun Solve.Request<E>.computeAll(
        x: Term,
        y: Term,
        z: Term
    ): Sequence<Solve.Response> {
        return sequenceOf(computeOne(x, y, z))
    }

    protected abstract fun Solve.Request<E>.computeOne(
        x: Term,
        y: Term,
        z: Term
    ): Solve.Response

}
