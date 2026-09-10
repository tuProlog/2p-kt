package it.unibo.tuprolog.solve.classic.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList

/**
 * ISO conjunction `','/2`: `(A, B) :- A, B.`, i.e. the body is the tuple `(A, B)` itself, which `toGoals`
 * unfolds back into the sequential goal stream `A` then `B`. Included in `StateRuleSelection`'s
 * `transparentToCut` set, so a cut occurring in `A` or `B` still cuts the enclosing clause's choice points, not
 * some choice point local to this rule.
 */
object Comma : RuleWrapper<ClassicExecutionContext>(",", 2) {
    override val Scope.head: KtList<Term>
        get() = listOf(varOf("A"), varOf("B"))

    override val Scope.body: Term
        get() = tupleOf(varOf("A"), varOf("B"))
}
