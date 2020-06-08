package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class BacktrackableSideEffect1<E : ExecutionContext>(typeName: String) : UnaryPredicate<E>(typeName) {
    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> =
        accept(request, request.arguments[0][request.context.substitution])

    abstract fun accept(request: Solve.Request<E>, term: Term): Sequence<Solve.Response>

    companion object {
        fun <E : ExecutionContext> of(typeName: String, consumer: (Solve.Request<E>, Term) -> Sequence<Solve.Response>): BacktrackableSideEffect1<E> =
            object : BacktrackableSideEffect1<E>(typeName) {
                override fun accept(request: Solve.Request<E>, term: Term): Sequence<Solve.Response> {
                    return consumer(request, term)
                }
            }
    }
}