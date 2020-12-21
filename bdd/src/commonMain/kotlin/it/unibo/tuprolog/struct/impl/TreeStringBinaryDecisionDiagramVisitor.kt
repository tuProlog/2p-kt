package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor

/**
 * Formats a [BinaryDecisionDiagram] as a tree-like string.
 *
 * @author Jason Dellaluce
 */
internal class TreeStringBinaryDecisionDiagramVisitor<T : Comparable<T>> : BinaryDecisionDiagramVisitor<T> {

    private var depth = 0
    val stringBuilder = StringBuilder()

    private fun printSpaces() {
        for (i in 0 until depth) {
            stringBuilder.append("\t")
        }
    }

    override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
        printSpaces()
        stringBuilder.appendLine(node.value)
    }

    override fun visit(node: BinaryDecisionDiagram.Var<T>) {
        printSpaces()
        stringBuilder.appendLine(node.value)
        depth++
        node.low.accept(this)
        node.high.accept(this)
        depth--
    }
}
