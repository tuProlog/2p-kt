package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.leaf.DirectiveIndex
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal class RootNode(
    clauses: Iterable<Clause>,
    override val isOrdered: Boolean
) : ReteTree {

    private val theoryCache: MutableList<Clause> = dequeOf()
    private var isCacheValid = true
    private val rules: RuleNode =
        RuleNode(isOrdered)
    private val directives: DirectiveIndex = DirectiveIndex(isOrdered)

    private var lowestIndex: Long = 0
    private var highestIndex: Long = 0

    init {
        clauses.forEach { assertZ(it) }
    }

    override val clauses: Sequence<Clause>
        get() = {
            regenerateCache()
            theoryCache.asSequence()
        }()

    override fun get(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) directives.get(clause)
        else rules.get(clause)

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        if (clause.isDirective) {
            directives.retractFirst(clause).let {
                invalidCache(it)
                it
            }
        }
        else {
            rules.retractFirst(clause).let {
                invalidCache(it)
                it
            }
        }

    override fun retractOnly(clause: Clause, limit: Int): Sequence<Clause> =
        (1 .. limit)
            .map { retractFirst(clause) }
            .asSequence()
            .flatMap { it }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if(clause.isDirective) {
            directives.retractAll(clause).let {
                invalidCache(it)
                it
            }
        } else {
            rules.retractAll(clause).let {
                invalidCache(it)
                it
            }
        }

    override fun deepCopy(): ReteTree =
        RootNode(clauses.toList(), isOrdered)

    override fun assertA(clause: Clause) {
        val indexed = assignLowerIndex(clause)

        if (isOrdered) {
            theoryCache.addFirst(clause)

            if (clause.isDirective) directives.assertA(indexed)
            else rules.assertA(indexed)
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: Clause) {
        val indexed = assignHigherIndex(clause)

        theoryCache.add(clause)
        if(clause.isDirective) directives.assertZ(indexed)
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

    private fun invalidCache(result: Sequence<*>) {
        if (result.any()) {
            theoryCache.clear()
            isCacheValid = false
        }
    }

    private fun regenerateCache() {
        if(!isCacheValid) {
            theoryCache.addAll(
                if(isOrdered) {
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
            isCacheValid = true
        }
    }

}