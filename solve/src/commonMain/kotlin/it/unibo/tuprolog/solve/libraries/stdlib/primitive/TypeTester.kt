package it.unibo.tuprolog.solve.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

abstract class TypeTester<E : ExecutionContext>(typeName: String) : UnaryPredicate<E>(typeName) {
    override fun uncheckedImplementation(request: Solve.Request<E>): Sequence<Solve.Response> = sequence {
        yield(
            with(request) {
                if (testType(arguments[0][context.substitution])) {
                    replySuccess()
                } else {
                    replyFail()
                }
            }
        )
    }

    abstract fun testType(term: Term): Boolean

    companion object {
        fun <E : ExecutionContext> of(typeName: String, tester: (Term) -> Boolean): TypeTester<E> =
            object : TypeTester<E>(typeName) {
                override fun testType(term: Term): Boolean = tester(term)
            }
    }
}