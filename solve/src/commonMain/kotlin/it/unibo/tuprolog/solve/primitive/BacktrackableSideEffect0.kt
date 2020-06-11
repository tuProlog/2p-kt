package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class BacktrackableSideEffect0<E : ExecutionContext>(typeName: String) : PredicateWithoutArguments<E>(typeName) {
    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        accept(request)

    abstract fun accept(request: Solve.Request<E>): Sequence<Solve.Response>

    companion object {
        fun <E : ExecutionContext> of(typeName: String, consumer: (Solve.Request<E>) -> Sequence<Solve.Response>): BacktrackableSideEffect0<E> =
            object : BacktrackableSideEffect0<E>(typeName) {
                override fun accept(request: Solve.Request<E>): Sequence<Solve.Response> {
                    return consumer(request)
                }
            }
    }
}