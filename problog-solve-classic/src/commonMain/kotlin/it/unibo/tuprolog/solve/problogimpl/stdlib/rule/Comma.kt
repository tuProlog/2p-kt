package it.unibo.tuprolog.solve.problogimpl.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

object Comma : RuleWrapper<ProblogClassicExecutionContext>(",", 2) {
    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("A"), varOf("B"))

    override val Scope.body: Term
        get() = tupleOf(varOf("A"), varOf("B"))
}
