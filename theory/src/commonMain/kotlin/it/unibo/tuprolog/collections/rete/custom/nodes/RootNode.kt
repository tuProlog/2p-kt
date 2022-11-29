package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.Cacheable
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.leaf.DirectiveIndex
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.dequeOf

internal class RootNode(
    override val unificator: Unificator,
    clauses: Iterable<Clause>,
    override val isOrdered: Boolean
) : ReteTree, Cacheable<Clause> {

    private val theoryCache: Cached<MutableList<Clause>> = Cached.of(this::regenerateCache)

    private val ruleIndex: RuleNode = RuleNode(unificator, isOrdered)

    private val directiveIndex: DirectiveIndex = DirectiveIndex(unificator, isOrdered)

    private var lowestIndex: Long = 0

    private var highestIndex: Long = 0

    init {
        clauses.forEach { assertZ(it) }
    }

    override val size: Int
        get() = directiveIndex.size + ruleIndex.size

    override val isEmpty: Boolean
        get() = directiveIndex.isEmpty && ruleIndex.isEmpty

    override val clauses: Sequence<Clause>
        get() = theoryCache.value.asSequence()

    override val directives: Sequence<Directive>
        get() = directiveIndex.getCache().map { it.value.castToDirective() }

    override val rules: Sequence<Rule>
        get() = ruleIndex.getCache().map { it.value.castToRule() }

    override fun get(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) directiveIndex.get(clause)
        else ruleIndex.get(clause)

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) {
            directiveIndex.retractFirst(clause)
        } else {
            ruleIndex.retractFirst(clause)
        }

    override fun retractOnly(clause: Clause, limit: Int): Sequence<Clause> =
        (1..limit).asSequence().flatMap { retractFirst(clause) }.buffered()

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) {
            directiveIndex.retractAll(clause)
        } else {
            ruleIndex.retractAll(clause)
        }

    override fun deepCopy(): ReteTree =
        RootNode(unificator, clauses.asIterable(), isOrdered)

    override fun assertA(clause: Clause) {
        val indexed = assignLowerIndex(clause)

        if (isOrdered) {
            theoryCache.ifValid {
                it.addFirst(clause)
            }

            if (clause.isDirective) directiveIndex.assertA(indexed)
            else ruleIndex.assertA(indexed)
        } else {
            throw UnsupportedOperationException("An unordered ReteTree cannot perform the assertA operation.")
        }
    }

    override fun assertZ(clause: Clause) {
        val indexed = assignHigherIndex(clause)

        theoryCache.ifValid {
            it.add(clause)
        }
        if (clause.isDirective) {
            directiveIndex.assertZ(indexed)
        } else {
            ruleIndex.assertZ(indexed)
        }
    }

    private fun assignHigherIndex(clause: Clause): IndexedClause =
        IndexedClause.of(++highestIndex, clause, this)

    private fun assignLowerIndex(clause: Clause): IndexedClause =
        IndexedClause.of(--lowestIndex, clause, this)

    private fun regenerateCache(): MutableList<Clause> {
        return dequeOf(
            if (isOrdered) {
                Utils.merge(
                    directiveIndex.getCache(),
                    ruleIndex.getCache()
                ).map { it.innerClause }
            } else {
                Utils.flatten(
                    directiveIndex.getCache().map { it.innerClause },
                    ruleIndex.getCache().map { it.innerClause }
                )
            }
        )
    }

    override fun getCache(): Sequence<Clause> {
        return theoryCache.value.asSequence()
    }

    override fun invalidateCache() {
        theoryCache.invalidate()
    }
}
