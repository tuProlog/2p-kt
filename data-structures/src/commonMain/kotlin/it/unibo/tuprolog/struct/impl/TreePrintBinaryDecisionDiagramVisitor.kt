package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor

class TreePrintBinaryDecisionDiagramVisitor<T>: BinaryDecisionDiagramVisitor<T> {

    private var index = 0

    override fun visit(value: Boolean) {
        printSpaces()
        println(value)
    }

    override fun visit(value: T, low: BinaryDecisionDiagram<T>, high: BinaryDecisionDiagram<T>) {
        printSpaces()
        println(value)
        index++
        low.accept(this)
        high.accept(this)
        index--
    }

    private fun printSpaces() {
        for(i in 0 until index) {
            print("\t")
        }
    }

}

fun <T> BinaryDecisionDiagramVisitor.Companion.ofTreePrint(): BinaryDecisionDiagramVisitor<T> =
    TreePrintBinaryDecisionDiagramVisitor<T>()