package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.magic.MagicCut
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

object Arrow : RuleWrapper<ExecutionContext>("->", 2) {
    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("Cond"), varOf("Then"))

    override val Scope.body: Term get() = tupleOf(
        structOf("call", varOf("Cond")),
        MagicCut,
        varOf("Then")
    )

}