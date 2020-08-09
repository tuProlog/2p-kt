package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.rule.RuleWrapper

object LeftArrow : RuleWrapper<ExecutionContext>("<-", 2) {

    private val Method by variableNames
    private val Ref by variableNames

    override val Scope.head: List<Term>
        get() = sequenceOf(
            varOf(Ref), varOf(Method)
        ).toList()

    override val Scope.body: Term
        get() = structOf(InvokeMethod.functor, varOf(Ref), varOf(Method), anonymous())
}