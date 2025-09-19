package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

sealed class Append : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {
    abstract override val Scope.head: KtList<Term>

    object Base : Append() {
        override val Scope.head: KtList<Term>
            get() =
                ktListOf(
                    emptyLogicList,
                    varOf("X"),
                    varOf("X"),
                )
    }

    object Recursive : Append() {
        override val Scope.head: KtList<Term>
            get() =
                ktListOf(
                    consOf(varOf("X"), varOf("Y")),
                    varOf("Z"),
                    consOf(varOf("X"), varOf("W")),
                )

        override val Scope.body: Term
            get() = structOf("append", varOf("Y"), varOf("Z"), varOf("W"))
    }

    companion object {
        const val FUNCTOR: String = "append"
        const val ARITY: Int = 3
        val SIGNATURE: Signature
            get() = Signature(FUNCTOR, ARITY)
    }
}
