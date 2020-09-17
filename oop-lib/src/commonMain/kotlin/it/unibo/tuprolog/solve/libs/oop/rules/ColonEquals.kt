package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.primitives.Assign
import it.unibo.tuprolog.solve.rule.RuleWrapper

object ColonEquals : RuleWrapper<ExecutionContext>(":=", 2) {

    private val Property by variableNames
    private val Value by variableNames
    private val Object by variableNames

    override val Scope.head: List<Term>
        get() = sequenceOf(
            structOf(LeftArrow.functor, varOf(Object), varOf(Property)),
            varOf(Value)
        ).toList()

    override val Scope.body: Term
        get() = structOf(Assign.functor, varOf(Object), varOf(Property), varOf(Value))
}
