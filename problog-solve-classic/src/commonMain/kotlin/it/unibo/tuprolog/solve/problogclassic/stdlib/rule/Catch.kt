package it.unibo.tuprolog.solve.problogclassic.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

object Catch : RuleWrapper<ProblogClassicExecutionContext>("catch", 3) {
    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("G"), varOf("E"), varOf("C"))

    override val Scope.body: Term
        get() = varOf("G")
}
