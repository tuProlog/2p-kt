package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.collections.rete.nodes.custom.index.DirectiveIndex
import it.unibo.tuprolog.collections.rete.nodes.custom.index.addFirst
import it.unibo.tuprolog.collections.rete.nodes.custom.index.dequeOf
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule

internal class FamilyRootNode(
    clauses: Iterable<Clause>,
    override val operationalOrder: Boolean
) : RootNode {

    private val children: MutableMap<Functor, ReteNode> = mutableMapOf()
    private val theory: MutableList<Clause> = dequeOf()

    private var lowestIndex: Long = 0
    private var highestIndex: Long = 0

    init {
        clauses.forEach { assertZ(it) }
    }

    override fun theory(): Sequence<Clause> =
        theory.asSequence()

    override fun get(clause: Clause): Sequence<Clause> =
        chooseBranch(clause, ReteNode::get)

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        chooseBranch(clause, ReteNode::retractFirst)

    override fun retractOnly(clause: Clause, limit: Int): Sequence<Clause> =
        (1 .. limit)
            .map { chooseBranch(clause, ReteNode::retractFirst) }
            .asSequence()
            .flatMap { it }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        chooseBranch(clause, ReteNode::retractAll)

    override fun deepCopy(): RootNode =
        FamilyRootNode(theory, operationalOrder)

    override fun assertA(clause: Clause) {
        val indexed = assignLowerIndex(clause)

        when (clause) {
            is Directive -> {
                theory.addFirst(clause)
                children.getOrPut(null) {
                    DirectiveIndex(
                        operationalOrder
                    )
                }
            }
            is Rule -> clause.head.functor.let {
                theory.addFirst(clause)
                children.getOrPut(it) {
                    FunctorNode(
                        operationalOrder,
                        0
                    )
                }
            }
            else -> null
        }?.assertA(indexed)
    }

    override fun assertZ(clause: Clause) {
        val indexed = assignHigherIndex(clause)

        when (clause) {
            is Directive -> {
                theory.add(clause)
                children.getOrPut(null) {
                    DirectiveIndex(
                        operationalOrder
                    )
                }
            }
            is Rule -> clause.head.functor.let {
                theory.add(clause)
                children.getOrPut(it) {
                    FunctorNode(
                        operationalOrder,
                        0
                    )
                }
            }
            else -> null
        }?.assertZ(indexed)
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

    private fun chooseBranch(clause: Clause, op: ReteNode.(Clause) -> Sequence<Clause>) : Sequence<Clause> =
        when (clause) {
            is Directive -> {
                children[null]
            }
            is Rule -> clause.head.functor.let {
                children[it]
            }
            else -> null
        }?.run { op(clause) } ?: emptySequence()

}