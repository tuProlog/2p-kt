package it.unibo.tuprolog.solve.prolog.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList

/**
 * ISO conjunction `','/2`: `(A, B) :- A, B.`, i.e. the body is the tuple `(A, B)` itself, which `toGoals`
 * unfolds back into the sequential goal stream `A` then `B`. Included in `StateRuleSelection`'s
 * `transparentToCut` set, so a cut occurring in `A` or `B` still cuts the enclosing clause's choice points, not
 * some choice point local to this rule.
 */
object Comma : RuleWrapper<PrologExecutionContext>(",", 2) {
    override val help: String =
        """
        `(+First, +Second)`

        Conjunction: proves `First` and then `Second`, backtracking into `First` for further solutions whenever `Second` fails. Conjunction is transparent to cut: a `!` occurring in either conjunct cuts the clause in which the conjunction appears.
        """.trimIndent()

    override val Scope.head: KtList<Term>
        get() = listOf(varOf("A"), varOf("B"))

    override val Scope.body: Term
        get() = tupleOf(varOf("A"), varOf("B"))
}
