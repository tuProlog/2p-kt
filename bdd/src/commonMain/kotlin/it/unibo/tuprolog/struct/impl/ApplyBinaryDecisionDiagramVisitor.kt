package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.BinaryDecisionDiagramVisitor

/**
 * This visitor implements the "Apply" [BinaryDecisionDiagram] construction algorithm. Although it is one of
 * the simplest solutions for BDD construction, it is able to produce Ordered Binary Decision Diagrams (OBDD).
 * The "apply" operation can be used to perform any boolean operation over BDDs.
 *
 * @author Jason Dellaluce
 */
internal sealed class ApplyBinaryDecisionDiagramVisitor<T : Comparable<T>> : BinaryDecisionDiagramVisitor<T> {
    var result: BinaryDecisionDiagram<T>? = null

    data class Unary<T : Comparable<T>>(
        private val operator: (first: Boolean) -> Boolean
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
            result = BinaryDecisionDiagram.Terminal(operator(node.value))
        }

        override fun visit(node: BinaryDecisionDiagram.Var<T>) {
            val lowVisitor = Unary<T>(operator)
            val highVisitor = Unary<T>(operator)
            node.low.accept(lowVisitor)
            node.high.accept(highVisitor)
            result = BinaryDecisionDiagram.Var(node.value, lowVisitor.result!!, highVisitor.result!!)
        }
    }

    data class Binary<T : Comparable<T>>(
        private val thatNode: BinaryDecisionDiagram<T>,
        private val operator: (first: Boolean, second: Boolean) -> Boolean
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
            result = when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    BinaryDecisionDiagram.Terminal(operator(node.value, thatNode.value))
                }
                is BinaryDecisionDiagram.Var -> {
                    val lowVisitor = Binary(thatNode.low, operator)
                    val highVisitor = Binary(thatNode.high, operator)
                    node.accept(lowVisitor)
                    node.accept(highVisitor)
                    BinaryDecisionDiagram.Var(
                        thatNode.value,
                        lowVisitor.result!!,
                        highVisitor.result!!,
                    )
                }
            }
        }

        override fun visit(node: BinaryDecisionDiagram.Var<T>) {
            result = when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    val visitor = Binary(node, operator)
                    thatNode.accept(visitor)
                    visitor.result!!
                }
                is BinaryDecisionDiagram.Var -> {
                    val newValue: T
                    val firstLow: BinaryDecisionDiagram<T>
                    val firstHigh: BinaryDecisionDiagram<T>
                    val secondLow: BinaryDecisionDiagram<T>
                    val secondHigh: BinaryDecisionDiagram<T>

                    if (node.value <= thatNode.value) {
                        newValue = node.value
                        firstHigh = node.high
                        firstLow = node.low
                    } else {
                        newValue = thatNode.value
                        firstLow = node
                        firstHigh = node
                    }
                    if (thatNode.value <= node.value) {
                        secondHigh = thatNode.high
                        secondLow = thatNode.low
                    } else {
                        secondLow = thatNode
                        secondHigh = thatNode
                    }

                    val lowVisitor = Binary(secondLow, operator)
                    val highVisitor = Binary(secondHigh, operator)
                    firstLow.accept(lowVisitor)
                    firstHigh.accept(highVisitor)
                    BinaryDecisionDiagram.Var(
                        newValue,
                        lowVisitor.result!!,
                        highVisitor.result!!
                    )
                }
            }
        }
    }
}
