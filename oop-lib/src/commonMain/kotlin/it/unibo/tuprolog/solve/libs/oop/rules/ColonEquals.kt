package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.Assign
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.Var

/**
 * ```prolog
 * ':='(R, M) :- var(R), !, fluent_reduce(M, R).
 * ':='(C, V) :- property_reduce(C, R, P), assign(R, P, V).
 * ```
 */
sealed class ColonEquals : RuleWrapper<ExecutionContext>(":=", 2) {

    object Invocation : ColonEquals() {

        private val R by variables
        private val M by variables

        override val Scope.head: List<Term>
            get() = kotlin.collections.listOf(R, M)

        override val Scope.body: Term
            get() = tupleOf(
                structOf(Var.functor, R),
                atomOf("!"),
                structOf(FluentReduce.FUNCTOR, M, R)
            )
    }

    object Assignment : ColonEquals() {
        private val C by variables
        private val P by variables
        private val R by variables
        private val V by variables

        override val Scope.head: List<Term>
            get() = kotlin.collections.listOf(C, V)

        override val Scope.body: Term
            get() = tupleOf(
                structOf(PropertyReduce.FUNCTOR, C, R, P),
                structOf(Assign.functor, R, P, V),
            )
    }
}
