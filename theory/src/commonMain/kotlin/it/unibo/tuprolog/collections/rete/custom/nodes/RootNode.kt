package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.Cacheable
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.leaf.DirectiveIndex
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal class RootNode(
    clauses: Iterable<Clause>,
    override val isOrdered: Boolean
) : ReteTree, Cacheable<Clause> {

    private val theoryCache: Cached<MutableList<Clause>> = Cached.of(this::regenerateCache)
    private val rules: RuleNode = RuleNode(isOrdered)
    private val directives: DirectiveIndex = DirectiveIndex(isOrdered)

    private var lowestIndex: Long = 0
    private var highestIndex: Long = 0

    init {
        clauses.forEach { assertZ(it) }
    }

    override val clauses: Sequence<Clause>
        get() =
            theoryCache.value.asSequence()

    override fun get(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) directives.get(clause)
        else rules.get(clause)

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) {
            directives.retractFirst(clause).invalidatingCacheIfNonEmpty()
        } else {
            rules.retractFirst(clause).invalidatingCacheIfNonEmpty()
        }

    override fun retractOnly(clause: Clause, limit: Int): Sequence<Clause> =
        (1..limit)
            .map { retractFirst(clause) }
            .asSequence()
            .flatMap { it }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) {
            directives.retractAll(clause).invalidatingCacheIfNonEmpty()
        } else {
            rules.retractAll(clause).invalidatingCacheIfNonEmpty()
        }

    override fun deepCopy(): ReteTree =
        RootNode(clauses.toList(), isOrdered)

    override fun assertA(clause: Clause) {
        val indexed = assignLowerIndex(clause)

        if (isOrdered) {
            theoryCache.ifValid {
                it.addFirst(clause)
            }

            if (clause.isDirective) directives.assertA(indexed)
            else rules.assertA(indexed)
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: Clause) {
        val indexed = assignHigherIndex(clause)

        theoryCache.ifValid {
            it.add(clause)
        }
        if (clause.isDirective) directives.assertZ(indexed)
        else rules.assertZ(indexed)
    }

    private fun assignHigherIndex(clause: Clause): IndexedClause =
        IndexedClause.of(
            ++highestIndex,
            clause
        )

    private fun assignLowerIndex(clause: Clause): IndexedClause =
        IndexedClause.of(
            --lowestIndex,
            clause
        )

    private fun regenerateCache(): MutableList<Clause> {
        return dequeOf(
            if (isOrdered) {
                Utils.merge(
                    directives.getCache(),
                    rules.getCache()
                ).map { it.innerClause }
            } else {
                Utils.flatten(
                    directives.getCache().map { it.innerClause },
                    rules.getCache().map { it.innerClause }
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