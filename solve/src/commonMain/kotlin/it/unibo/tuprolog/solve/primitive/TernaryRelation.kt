package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/** Base class to implement primitives that relate tree [Term]s */
abstract class TernaryRelation<E : ExecutionContext>(operator: String) : PrimitiveWrapper<E>(operator, 3) {

    /** Template method that should compute the response of the [Term] relation application  */
    protected abstract fun Solve.Request<E>.computeAll(
        x: Term,
        y: Term,
        z: Term
    ): Sequence<Solve.Response>

    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> {
        return request.computeAll(request.arguments[0], request.arguments[1], request.arguments[2])
    }
}
