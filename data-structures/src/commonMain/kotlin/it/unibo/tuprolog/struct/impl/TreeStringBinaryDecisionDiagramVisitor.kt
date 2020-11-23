package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor

internal class TreeStringBinaryDecisionDiagramVisitor<T : Comparable<T>> : BinaryDecisionDiagramVisitor<T> {

    private var index = 0
    val stringBuilder = StringBuilder()

    override fun visit(value: Boolean) {
        printSpaces()
        stringBuilder.appendLine(value)
    }

    override fun visit(value: T, low: BinaryDecisionDiagram<T>, high: BinaryDecisionDiagram<T>) {
        printSpaces()
        stringBuilder.appendLine(value)
        index++
        low.accept(this)
        high.accept(this)
        index--
    }

    private fun printSpaces() {
        for (i in 0 until index) {
            stringBuilder.append("\t")
        }
    }
}

fun <T : Comparable<T>> BinaryDecisionDiagramVisitor.Companion.ofTreePrint(): BinaryDecisionDiagramVisitor<T> =
    TreeStringBinaryDecisionDiagramVisitor<T>()

fun <T : Comparable<T>> BinaryDecisionDiagram<T>.toTreeString(): String {
    val visitor = TreeStringBinaryDecisionDiagramVisitor<T>()
    this.accept(visitor)
    return visitor.stringBuilder.toString()
}
