package it.unibo.tuprolog.libraries.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.stdlib.magic.MagicCut
import it.unibo.tuprolog.rule.RuleWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

sealed class NegationAsFailure : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {

    override val Scope.head: KtList<Term>
        get() = ktListOf(
            varOf("X")
        )

    abstract override val Scope.body: Term

    object Fail : NegationAsFailure() {
        override val Scope.body: Term
            get() = tupleOf(
                varOf("X"),
                MagicCut,
                truthOf(false)
            )
    }

    object Success : NegationAsFailure() {
        override val Scope.body: Term
            get() = whatever()
    }

    companion object {
        const val FUNCTOR: String = "\\+"

        const val ARITY: Int = 1
    }
}