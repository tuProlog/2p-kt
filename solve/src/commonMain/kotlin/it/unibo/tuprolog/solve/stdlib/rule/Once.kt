package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

object Once : RuleWrapper<ExecutionContext>("once", 1) {

    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("G"))

    override val Scope.body: Term
        get() = tupleOf(
            structOf("call", varOf("G")),
            atomOf("!")
        )
}