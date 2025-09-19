package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * ```prolog
 * fluent_reduce([P, M | X], R) :- !, invoke_method(P, M, P1), fluent_reduce([P1 | X], R). % recursive
 * fluent_reduce([P | M], R) :- !, invoke_method(P, M, R).                                 % couple
 * fluent_reduce([R], R) :- !.                                                             % base
 * fluent_reduce(R, R) :- !.                                                               % trivial
 * ```
 */
@Suppress("PropertyName")
sealed class FluentReduce : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {
    companion object {
        const val FUNCTOR = "fluent_reduce"
        const val ARITY = 2
    }

    protected val R by variables

    override val Scope.body: Term
        get() = atomOf("!")

    object Recursive : FluentReduce() {
        private val P by variables
        private val M by variables
        private val P1 by variables
        private val X by variables

        override val Scope.head: List<Term>
            get() =
                kotlin.collections.listOf(
                    logicListFrom(P, M, last = X),
                    R,
                )

        override val Scope.body: Term
            get() =
                tupleOf(
                    atomOf("!"),
                    structOf(InvokeMethod.functor, P, M, P1),
                    structOf(FUNCTOR, consOf(P1, X), R),
                )
    }

    object Couple : FluentReduce() {
        private val P by variables
        private val M by variables

        override val Scope.head: List<Term>
            get() = kotlin.collections.listOf(consOf(P, M), R)

        override val Scope.body: Term
            get() =
                tupleOf(
                    atomOf("!"),
                    structOf(InvokeMethod.functor, P, M, R),
                )
    }

    // object Base : FluentReduce() {
    //     override val Scope.head: List<Term>
    //         get() = kotlin.collections.listOf(
    //             listOf(R),
    //             R
    //         )
    // }

    object Trivial : FluentReduce() {
        override val Scope.head: List<Term>
            get() = kotlin.collections.listOf(R, R)
    }
}
