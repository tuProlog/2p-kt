package it.unibo.tuprolog.datalog

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermVisitor

/**
 * A [TermVisitor] specialised for walking a [Clause] literal by literal, rather than term by term: instead
 * of overriding [TermVisitor.visitClause] directly, implementors get separate hooks for the clause's head
 * and for each body literal, further split into the negated ([visitNegatedLiteral]) and non-negated
 * ([visitNonNegatedLiteral]) case — the very distinction the Datalog well-formedness checks in
 * [it.unibo.tuprolog.datalog] are built on (see [it.unibo.tuprolog.datalog.isNegated]).
 *
 * [it.unibo.tuprolog.datalog.visitors.AbstractClauseVisitor] is the base implementation that does the actual
 * dispatching (head vs. body, negated vs. non-negated) on top of this interface;
 * [it.unibo.tuprolog.datalog.visitors.CompoundFinder] is a concrete example.
 */
interface ClauseVisitor<T> : TermVisitor<T> {
    /** Visits the head of a [Clause] (a no-op wrapper around [visitLiteral] by default). */
    fun visitHead(head: Struct): T = visitLiteral(head)

    /** Visits any literal (head or body) of a [Clause], regardless of whether it is negated. */
    fun visitLiteral(literal: Struct): T

    /** Visits a body literal known not to be negated. Falls back to [visitLiteral]. */
    fun visitNonNegatedLiteral(literal: Struct): T = visitLiteral(literal)

    /**
     * Visits a body literal known to be negated, receiving the literal *inside* the `not(...)`/`\+(...)`
     * wrapper (not the wrapper itself). Falls back to [visitLiteral].
     */
    fun visitNegatedLiteral(literal: Struct): T = visitLiteral(literal)

    override fun visitClause(term: Clause): T
}
