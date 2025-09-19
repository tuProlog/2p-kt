package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * ```prolog
 * property_reduce([A, B | C], O, P) :- !, invoke_method(A, B, B1), property_reduce([B1 | C], O, P). % recursive
 * property_reduce([A | B], A, B) :- !.                                                              % base
 * ```
 */
@Suppress("PropertyName")
sealed class PropertyReduce : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {
    companion object {
        const val FUNCTOR = "property_reduce"
        const val ARITY = 3
    }

    protected val A by variables
    protected val B by variables

    override val Scope.body: Term
        get() = atomOf("!")

    object Recursive : PropertyReduce() {
        private val B1 by variables
        private val C by variables
        private val O by variables
        private val P by variables

        override val Scope.head: List<Term>
            get() = listOf(logicListFrom(A, B, last = C), O, P)

        override val Scope.body: Term
            get() =
                tupleOf(
                    atomOf("!"),
                    structOf(InvokeMethod.functor, A, B, B1),
                    structOf(FUNCTOR, consOf(B1, C), O, P),
                )
    }

    object Base : PropertyReduce() {
        override val Scope.head: List<Term>
            get() = listOf(consOf(A, B), A, B)
    }
}
