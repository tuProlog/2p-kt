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
) : BinaryDecisionDiagramVisitor<T> {
    var result: E? = null

    override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
        result = if (node.value) trueTerminal else falseTerminal
    }

    override fun visit(node: BinaryDecisionDiagram.Var<T>) {
        val lowVisitor = ExpansionBinaryDecisionDiagramVisitor(varOp, falseTerminal, trueTerminal)
        val highVisitor = ExpansionBinaryDecisionDiagramVisitor(varOp, falseTerminal, trueTerminal)
        node.low.accept(lowVisitor)
        node.high.accept(highVisitor)
        result = varOp(node.value, lowVisitor.result!!, highVisitor.result!!)
    }
}
