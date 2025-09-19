package it.unibo.tuprolog.bdd.impl.builder

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramBuilder
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramBuilder.Companion.defaultOf

/**
 * Returns a [BinaryDecisionDiagramBuilder] instance that applies
 * reduction optimizations through the `reduce` algorithm, and
 * delegates the construction of each node to [delegate]. By default,
 * [delegate] is set as [defaultOf]. The following reductions are
 * performed:
 * - Removal of duplicate variable nodes
 * - Removal of duplicate terminal nodes
 * - Removal of redundant variable nodes, which are
 * [BinaryDecisionDiagram.Variable] nodes where low and high point
 * to the same node
 * */
internal class ReducedBinaryDecisionDiagramBuilder<T : Comparable<T>>(
    private val delegate: BinaryDecisionDiagramBuilder<T>,
) : BinaryDecisionDiagramBuilder<T> {
    private val table: MutableMap<Int, BinaryDecisionDiagram<T>> = mutableMapOf()

    override fun buildVariable(
        value: T,
        low: BinaryDecisionDiagram<T>,
        high: BinaryDecisionDiagram<T>,
    ): BinaryDecisionDiagram<T> {
        if (low == high) {
            return low
        }

        val newNode = delegate.buildVariable(value, low, high)
        val key = newNode.hashCode()
        val cached = table[key]
        return if (cached != null) {
            cached
        } else {
            table[key] = newNode
            newNode
        }
    }

    override fun buildTerminal(truth: Boolean): BinaryDecisionDiagram<T> = delegate.buildTerminal(truth)
}
