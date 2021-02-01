package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

/**
 * Applies a given operation over a [BinaryDecisionDiagram] using the Shannon Expansion.
 * The result is a reduction of a given diagram, determined by the provided recursive operation.
 *
 * @author Jason Dellaluce
 */
internal class ExpansionBinaryDecisionDiagramVisitor<T : Comparable<T>, E>(
    private val varOp: (node: T, low: E, high: E) -> E,
    private val falseTerminal: E,
    private val trueTerminal: E
) : BinaryDecisionDiagramVisitor<T, E> {

    override fun visit(node: BinaryDecisionDiagram.Terminal<T>): E {
        return if (node.value) trueTerminal else falseTerminal
    }

    override fun visit(node: BinaryDecisionDiagram.Variable<T>): E {
        val lowValue = node.low.accept(this)
        val highValue = node.high.accept(this)
        return varOp(node.value, lowValue, highValue)
    }
}
