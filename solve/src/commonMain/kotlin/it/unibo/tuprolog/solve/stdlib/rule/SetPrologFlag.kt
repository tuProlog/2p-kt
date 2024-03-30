package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import kotlin.collections.List as KtList

object SetPrologFlag : RuleWrapper<ExecutionContext>("set_prolog_flag", 2) {
    override val Scope.head: KtList<Term>
        get() = listOf(varOf("Key"), varOf("Value"))

    override val Scope.body: Term get() = structOf(SetFlag.functor, varOf("Key"), varOf("Value"))
}
