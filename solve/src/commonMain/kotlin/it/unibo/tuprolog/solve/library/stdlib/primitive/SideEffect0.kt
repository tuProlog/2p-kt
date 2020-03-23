package it.unibo.tuprolog.solve.library.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class SideEffect0<E : ExecutionContext>(typeName: String) : PredicateWithoutArguments<E>(typeName) {
    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> = sequence {
        yield(accept(request))
    }

    abstract fun accept(request: Solve.Request<E>): Solve.Response

    companion object {
        fun <E : ExecutionContext> of(typeName: String, consumer: (Solve.Request<E>) -> Solve.Response): SideEffect0<E> =
            object : SideEffect0<E>(typeName) {
                override fun accept(request: Solve.Request<E>): Solve.Response {
                    return consumer(request)
                }
            }
    }
}