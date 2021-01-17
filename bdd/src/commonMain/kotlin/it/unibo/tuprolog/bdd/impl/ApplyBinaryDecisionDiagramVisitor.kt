package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

/**
 * This visitor implements the "Apply" [BinaryDecisionDiagram] construction algorithm. Although it is one of
 * the simplest solutions for BDD construction, it is able to produce Reduced Ordered Binary Decision Diagrams (ROBDD).
 * The "apply" operation can be used to perform any boolean operation over BDDs.
 *
 * The basic algorithm has been modified to support BDD reductions, and performs them directly while constructing
 * each diagram node. The following reductions are performed:
 * - Removal of duplicate variable nodes
 * - Removal of duplicate terminal nodes
 * - Removal of redundant variable nodes, which are [BinaryDecisionDiagram.Var] nodes where low and high point
 * to the same node
 *
 * @author Jason Dellaluce
 */
internal sealed class ApplyBinaryDecisionDiagramVisitor<T : Comparable<T>> : BinaryDecisionDiagramVisitor<T> {
    var result: BinaryDecisionDiagram<T>? = null

    /* Reduction: This avoids duplicated nodes, both variables and terminals */
    protected fun getResultUsingTable(
        key: Int,
        value: BinaryDecisionDiagram<T>,
        table: MutableMap<Int, BinaryDecisionDiagram<T>>,
    ): BinaryDecisionDiagram<T> {
        val tableValue = table[key]
        return if (tableValue != null) {
            tableValue
        } else {
            table[key] = value
            value
        }
    }

    /* Reduction: This avoids redundant nodes */
    protected fun createVarNode(
        value: T,
        low: BinaryDecisionDiagram<T> = BinaryDecisionDiagram.Terminal(false),
        high: BinaryDecisionDiagram<T> = BinaryDecisionDiagram.Terminal(true)
    ): BinaryDecisionDiagram<T> {
        /* NOTE: This check is faster than invoking equals, but it makes sense only
           because of the getResultUsingTable function, which guarantees nodes to
           have "unique" instances over the diagram. */
        return if (low === high) {
            low
        } else {
            BinaryDecisionDiagram.Var(value, low, high)
        }
    }

    data class Unary<T : Comparable<T>>(
        private val operator: (first: Boolean) -> Boolean,
        private val table: MutableMap<Int, BinaryDecisionDiagram<T>> = mutableMapOf()
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
            val nodeValue = operator(node.value)
            val nodeKey = if (nodeValue) Int.MAX_VALUE else Int.MIN_VALUE
            result = getResultUsingTable(nodeKey, BinaryDecisionDiagram.Terminal(nodeValue), table)
        }

        override fun visit(node: BinaryDecisionDiagram.Var<T>) {
            val lowVisitor = Unary(operator, table)
            val highVisitor = Unary(operator, table)
            node.low.accept(lowVisitor)
            node.high.accept(highVisitor)

            val nodeKey = 31 * node.value.hashCode() +
                31 * lowVisitor.result!!.hashCode() +
                31 * highVisitor.result!!.hashCode()
            result = getResultUsingTable(
                nodeKey,
                createVarNode(node.value, lowVisitor.result!!, highVisitor.result!!),
                table
            )
        }
    }

    data class Binary<T : Comparable<T>>(
        private val thatNode: BinaryDecisionDiagram<T>,
        private val operator: (first: Boolean, second: Boolean) -> Boolean,
        private val table: MutableMap<Int, BinaryDecisionDiagram<T>> = mutableMapOf()
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>) {
            result = when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    val nodeValue = operator(node.value, thatNode.value)
                    val nodeKey = if (nodeValue) Int.MAX_VALUE else Int.MIN_VALUE
                    getResultUsingTable(nodeKey, BinaryDecisionDiagram.Terminal(nodeValue), table)
                }
                is BinaryDecisionDiagram.Var -> {
                    val lowVisitor = Binary(thatNode.low, operator, table)
                    val highVisitor = Binary(thatNode.high, operator, table)
                    node.accept(lowVisitor)
                    node.accept(highVisitor)

                    val nodeKey = 31 * thatNode.hashCode() +
                        31 * lowVisitor.result!!.hashCode() +
                        31 * highVisitor.result!!.hashCode()
                    getResultUsingTable(
                        nodeKey,
                        createVarNode(
                            thatNode.value,
                            lowVisitor.result!!,
                            highVisitor.result!!,
                        ),
                        table
                    )
                }
            }
        }

        override fun visit(node: BinaryDecisionDiagram.Var<T>) {
            result = when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    val visitor = Binary(node, operator, table)
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

                    val lowVisitor = Binary(secondLow, operator, table)
                    val highVisitor = Binary(secondHigh, operator, table)
                    firstLow.accept(lowVisitor)
                    firstHigh.accept(highVisitor)

                    val nodeKey = 31 * newValue.hashCode() +
                        31 * lowVisitor.result!!.hashCode() +
                        31 * highVisitor.result!!.hashCode()
                    getResultUsingTable(
                        nodeKey,
                        createVarNode(
                            newValue,
                            lowVisitor.result!!,
                            highVisitor.result!!
                        ),
                        table
                    )
                }
            }
        }
    }
}
