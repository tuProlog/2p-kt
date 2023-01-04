package it.unibo.tuprolog.datalog.visitors

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.visitors.ExhaustiveTermVisitor
import it.unibo.tuprolog.datalog.ClauseVisitor
import it.unibo.tuprolog.datalog.asLiteral
import it.unibo.tuprolog.datalog.isNegated

abstract class AbstractClauseVisitor<T> : ClauseVisitor<T>, ExhaustiveTermVisitor<T>() {
    override fun visitLiteral(literal: Struct): T =
        literal.argsSequence.map {
            if (it.isClause) visitStruct(it.castToStruct()) else it.accept(this)
        }.let { reduce(it) }

    private fun dispatchHead(head: Struct): T = visitHead(head)

    private fun dispatchLiteral(literal: Struct): T =
        if (literal.isNegated) {
            visitNegatedLiteral(literal[0].asLiteral())
        } else {
            visitNonNegatedLiteral(literal)
        }

    protected fun dispatchHead(clause: Clause): Sequence<T> =
        sequenceOf(clause.head).filterNotNull().map { dispatchHead(it) }

    protected fun dispatchBody(clause: Clause): Sequence<T> =
        clause.bodyItems.asSequence().map {
            dispatchLiteral(it.asLiteral(ofClause = clause))
        }

    override fun visitClause(term: Clause): T = reduce(dispatchHead(term) + dispatchBody(term))

    protected abstract fun reduce(results: Sequence<T>): T
}
