package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class SideEffect1<E : ExecutionContext>(typeName: String) : UnaryPredicate<E>(typeName) {
    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> = sequence {
        yield(
            with(request) {
                accept(request, arguments[0][context.substitution])
            }
        )
    }

    abstract fun accept(request: Solve.Request<E>, term: Term): Solve.Response

    companion object {
        fun <E : ExecutionContext> of(typeName: String, consumer: (Solve.Request<E>, Term) -> Solve.Response): SideEffect1<E> =
            object : SideEffect1<E>(typeName) {
                override fun accept(request: Solve.Request<E>, term: Term): Solve.Response {
                    return consumer(request, term)
                }
            }
    }
}