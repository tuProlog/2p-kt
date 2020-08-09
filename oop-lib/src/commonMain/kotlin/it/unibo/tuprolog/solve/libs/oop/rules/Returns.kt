package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod
import it.unibo.tuprolog.solve.rule.RuleWrapper

object Returns : RuleWrapper<ExecutionContext>("returns", 2) {

    private val Method by variableNames
    private val Res by variableNames
    private val Ref by variableNames

    override val Scope.head: List<Term>
        get() = sequenceOf(
            structOf(LeftArrow.functor, varOf(Ref), varOf(Method)), varOf(Res)
        ).toList()

    override val Scope.body: Term
        get() = tupleOf(
            structOf(InvokeMethod.functor, varOf(Ref), varOf(Method), varOf(Res))
        )

}