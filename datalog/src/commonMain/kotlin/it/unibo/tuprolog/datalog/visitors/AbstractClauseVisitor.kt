package it.unibo.tuprolog.datalog.visitors

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.visitors.ExhaustiveTermVisitor
import it.unibo.tuprolog.datalog.ClauseVisitor
import it.unibo.tuprolog.datalog.asLiteral
import it.unibo.tuprolog.datalog.isNegated

/**
 * Base [ClauseVisitor] that implements the head/body and negated/non-negated dispatching itself, so
 * subclasses only need to fold per-literal results together (via [reduce]) and, typically, override the
 * leaf-level `visitX` methods inherited from [ExhaustiveTermVisitor] to inspect the literals' arguments.
 *
 * [visitClause] walks a [Clause]'s head (if any) and every body item — each normalised to a literal via
 * [it.unibo.tuprolog.datalog.asLiteral] — dispatching negated body literals (see [isNegated]) to
 * [ClauseVisitor.visitNegatedLiteral] with the literal *inside* the `not(...)`/`\+(...)` wrapper, and
 * everything else to [ClauseVisitor.visitNonNegatedLiteral]/[ClauseVisitor.visitHead]; [visitLiteral] then
 * recurses into each of that literal's arguments and folds the per-argument results with [reduce].
 * [CompoundFinder] and [HeadVariablesOutsideNonNegatedLiterals] are the two concrete visitors built on top
 * of this class.
 */
abstract class AbstractClauseVisitor<T> :
    ExhaustiveTermVisitor<T>(),
    ClauseVisitor<T> {
    override fun visitLiteral(literal: Struct): T =
        literal.argsSequence
            .map {
                if (it.isClause) visitStruct(it.castToStruct()) else it.accept(this)
            }.let { reduce(it) }

    private fun dispatchHead(head: Struct): T = visitHead(head)

    private fun dispatchLiteral(literal: Struct): T =
        if (literal.isNegated) {
            visitNegatedLiteral(literal[0].asLiteral())
        } else {
            visitNonNegatedLiteral(literal)
        }

    /** Visits [clause]'s head, if any, yielding an empty [Sequence] for a headless [Clause]. */
    protected fun dispatchHead(clause: Clause): Sequence<T> =
        sequenceOf(clause.head).filterNotNull().map {
            dispatchHead(it)
        }

    /**
     * Visits every body item of [clause], dispatched to [ClauseVisitor.visitNegatedLiteral] or
     * [ClauseVisitor.visitNonNegatedLiteral] as appropriate.
     */
    protected fun dispatchBody(clause: Clause): Sequence<T> =
        clause.bodyItems.asSequence().map {
            dispatchLiteral(it.asLiteral(ofClause = clause))
        }

    override fun visitClause(term: Clause): T = reduce(dispatchHead(term) + dispatchBody(term))

    /** Folds the per-literal (or per-argument) results collected while visiting a clause into a single [T]. */
    protected abstract fun reduce(results: Sequence<T>): T
}
