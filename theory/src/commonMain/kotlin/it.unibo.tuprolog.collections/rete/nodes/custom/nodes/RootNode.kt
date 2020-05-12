package it.unibo.tuprolog.collections.rete.nodes.custom.nodes

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteTree
import it.unibo.tuprolog.collections.rete.nodes.custom.leaf.DirectiveIndex
import it.unibo.tuprolog.collections.rete.nodes.custom.leaf.dequeOf
import it.unibo.tuprolog.core.Clause

internal class RootNode(
    clauses: Iterable<Clause>,
    override val operationalOrder: Boolean
) : ReteTree {

    private val theory: MutableList<Clause> = dequeOf()
    private val rules: RuleNode =
        RuleNode(operationalOrder)
    private val directives: DirectiveIndex = DirectiveIndex(operationalOrder)

    private var lowestIndex: Long = 0
    private var highestIndex: Long = 0

    init {
        clauses.forEach { assertZ(it) }
    }

    override fun theory(): Sequence<Clause> =
        theory.asSequence()

    override fun get(clause: Clause): Sequence<Clause> =
        if(clause.isDirective) directives.get(clause)
        else rules.get(clause)

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        if(clause.isDirective) directives.retractFirst(clause)
        else rules.retractFirst(clause)

    override fun retractOnly(clause: Clause, limit: Int): Sequence<Clause> =
        (1 .. limit)
            .map { retractFirst(clause) }
            .asSequence()
            .flatMap { it }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if(clause.isDirective) directives.retractAll(clause)
        else rules.retractAll(clause)

    override fun deepCopy(): ReteTree =
        RootNode(theory, operationalOrder)

    override fun assertA(clause: Clause) {
        val indexed = assignLowerIndex(clause)

        if(operationalOrder){
            if(clause.isDirective) directives.assertA(indexed)
            else rules.assertA(indexed)
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: Clause) {
        val indexed = assignHigherIndex(clause)

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

}