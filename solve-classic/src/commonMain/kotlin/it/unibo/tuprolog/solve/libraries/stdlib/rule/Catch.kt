package it.unibo.tuprolog.solve.libraries.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.ExecutionContextImpl
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

object Catch : RuleWrapper<ExecutionContextImpl>("catch", 3) {
    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("G"), varOf("E"), varOf("C"))

    override val Scope.body: Term
        get() = varOf("G")
}