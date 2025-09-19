package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOP.CALL_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.OOP.CAST_OPERATOR
import it.unibo.tuprolog.solve.libs.oop.primitives.Assign
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.Var
import it.unibo.tuprolog.solve.libs.oop.primitives.Cast as CastPrimitive

/**
 * ```prolog
 * ':='(R, as(X, T)) :- var(R), !, fluent_reduce(X, Y), cast(X, T, R).
 * ':='(R, M) :- var(R), !, fluent_reduce(M, R).
 * ':='(C, V) :- property_reduce(C, R, P), assign(R, P, V).
 * ```
 */
sealed class ColonEquals : RuleWrapper<ExecutionContext>(CALL_OPERATOR, 2) {
    object Cast : ColonEquals() {
        private val R by variables
        private val X by variables
        private val Y by variables
        private val T by variables

        override val Scope.head: List<Term>
            get() = listOf(R, structOf(CAST_OPERATOR, X, T))

        override val Scope.body: Term
            get() =
                tupleOf(
                    structOf(Var.functor, R),
                    atomOf("!"),
                    structOf(FluentReduce.FUNCTOR, X, Y),
                    structOf(CastPrimitive.functor, Y, T, R),
                )
    }

    object Invocation : ColonEquals() {
        private val R by variables
        private val M by variables

        override val Scope.head: List<Term>
            get() = listOf(R, M)

        override val Scope.body: Term
            get() =
                tupleOf(
                    structOf(Var.functor, R),
                    atomOf("!"),
                    structOf(FluentReduce.FUNCTOR, M, R),
                )
    }

    object Assignment : ColonEquals() {
        private val C by variables
        private val P by variables
        private val R by variables
        private val V by variables

        override val Scope.head: List<Term>
            get() = listOf(C, V)

        override val Scope.body: Term
            get() =
                tupleOf(
                    structOf(PropertyReduce.FUNCTOR, C, R, P),
                    structOf(Assign.functor, R, P, V),
                )
    }
}
