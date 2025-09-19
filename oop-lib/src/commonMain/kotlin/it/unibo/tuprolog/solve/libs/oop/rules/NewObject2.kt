package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.NewObject3
import it.unibo.tuprolog.solve.rule.RuleWrapper

object NewObject2 : RuleWrapper<ExecutionContext>(NewObject3.functor, 2) {
    private val Type by variables
    private val Instance by variables

    override val Scope.head: List<Term>
        get() = listOf(Type, Instance)

    override val Scope.body: Term
        get() = structOf(NewObject3.functor, Type, emptyLogicList, Instance)
}
