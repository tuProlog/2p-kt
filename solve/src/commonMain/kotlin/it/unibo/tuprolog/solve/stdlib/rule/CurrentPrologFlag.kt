package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag
import kotlin.collections.List as KtList

object CurrentPrologFlag : RuleWrapper<ExecutionContext>("current_prolog_flag", 2) {
    override val Scope.head: KtList<Term>
        get() = listOf(varOf("Key"), varOf("Value"))

    override val Scope.body: Term get() = structOf(CurrentFlag.functor, varOf("Key"), varOf("Value"))
}
