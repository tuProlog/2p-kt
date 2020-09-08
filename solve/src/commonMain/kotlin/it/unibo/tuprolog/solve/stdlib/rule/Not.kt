package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

object Not : RuleWrapper<ExecutionContext>("not", 1) {
    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("G"))

    override val Scope.body: Term
        get() = tupleOf(
            structOf(EnsureExecutable.functor, varOf("G")),
            structOf("\\+", varOf("G"))
        )
}