package it.unibo.tuprolog.solve.libs.oop.rules

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.oop.OOP.ACCESS_OPERATOR
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * ```prolog
 * '.'(Ref, Method) :- fluent_reduce([Ref | Method], _)
 * ```
 *
 */
object Dot : RuleWrapper<ExecutionContext>(ACCESS_OPERATOR, 2) {
    private val Method by variables
    private val Ref by variables

    override val Scope.head: List<Term>
        get() =
            kotlin.collections.listOf(
                Ref,
                Method,
            )

    override val Scope.body: Term
        get() = structOf(FluentReduce.FUNCTOR, consOf(Ref, Method), `_`)
}
