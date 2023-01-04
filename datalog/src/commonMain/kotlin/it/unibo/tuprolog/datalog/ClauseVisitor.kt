package it.unibo.tuprolog.datalog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermVisitor

interface ClauseVisitor<T> : TermVisitor<T> {
    fun visitHead(head: Struct): T = visitLiteral(head)

    fun visitLiteral(literal: Struct): T

    fun visitNonNegatedLiteral(literal: Struct): T = visitLiteral(literal)

    fun visitNegatedLiteral(literal: Struct): T = visitLiteral(literal)

    override fun visitClause(term: Clause): T
}
