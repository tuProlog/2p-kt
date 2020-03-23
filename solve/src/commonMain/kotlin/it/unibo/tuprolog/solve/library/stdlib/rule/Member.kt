package it.unibo.tuprolog.solve.library.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

sealed class Member : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {

    abstract override val Scope.head: KtList<Term>

    object Base : Member() {
        override val Scope.head: KtList<Term>
            get() = ktListOf(
                varOf("H"),
                consOf(varOf("H"), whatever())
            )
    }

    object Recursive : Member() {
        override val Scope.head: KtList<Term>
            get() = ktListOf(
                varOf("H"),
                consOf(whatever(), varOf("T"))
            )

        override val Scope.body: Term
            get() = structOf("member", varOf("H"), varOf("T"))
    }

    companion object {
        const val FUNCTOR: String = "member"

        const val ARITY: Int = 2
    }
}