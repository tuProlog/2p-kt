package it.unibo.tuprolog.solve.classic.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList

/**
 * ISO `catch/3`: `catch(Goal, Catcher, Recovery) :- Goal.` -- the body just proves `Goal`; the actual
 * catch/recovery behaviour is not implemented here but in `StateException`, which climbs the execution-context
 * stack looking for a currently-executing goal shaped like this one whose `Catcher` unifies with a raised
 * exception, and if so proves `Recovery` instead.
 */
object Catch : RuleWrapper<ClassicExecutionContext>("catch", 3) {
    override val Scope.head: KtList<Term>
        get() = listOf(varOf("G"), varOf("E"), varOf("C"))

    override val Scope.body: Term
        get() = varOf("G")
}
