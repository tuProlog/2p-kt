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
 * - Removal of redundant variable nodes, which are [BinaryDecisionDiagram.Variable] nodes where low and high point
 * to the same node
 *
 * @author Jason Dellaluce
 */
internal sealed class ApplyBinaryDecisionDiagramVisitor<T : Comparable<T>> :
    BinaryDecisionDiagramVisitor<T, BinaryDecisionDiagram<T>> {
    /* Reduction: This avoids duplicated nodes, both variables and terminals.
    * NOTE: This also calls hashCode(), which initializes the lazy value that
    * will then be cached for fast later access. This is fundamental for reaching
    * O(1) on hashCode function even though this data structure is recursive. */
    protected fun getResultUsingTable(
        value: BinaryDecisionDiagram<T>,
        table: MutableMap<Int, BinaryDecisionDiagram<T>>,
    ): BinaryDecisionDiagram<T> {
        val key = value.hashCode()
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
        low: BinaryDecisionDiagram<T> = BinaryDecisionDiagram.ofTerminal(false),
        high: BinaryDecisionDiagram<T> = BinaryDecisionDiagram.ofTerminal(true)
    ): BinaryDecisionDiagram<T> {
        /* NOTE: This check is faster than invoking equals, but it makes sense only
           because of the getResultUsingTable function, which guarantees nodes to
           have "unique" instances over the diagram. */
        return if (low === high) {
            low
        } else {
            BinaryDecisionDiagram.ofVariable(value, low, high)
        }
    }

    data class Unary<T : Comparable<T>>(
        private val operator: (first: Boolean) -> Boolean,
        private val table: MutableMap<Int, BinaryDecisionDiagram<T>> = mutableMapOf()
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>): BinaryDecisionDiagram<T> {
            return getResultUsingTable(BinaryDecisionDiagram.ofTerminal(operator(node.value)), table)
        }

        override fun visit(node: BinaryDecisionDiagram.Variable<T>): BinaryDecisionDiagram<T> {
            val lowNode = node.low.accept(this)
            val highNode = node.high.accept(this)
            return getResultUsingTable(createVarNode(node.value, lowNode, highNode), table)
        }
    }

    /* NOTE: This implementation of the Binary visitor does not require the creation of other sub-visitors,
    however it is NOT thread-safe due to its operand parameters being configurable. If we ever decide to move
    to a parallelized optimization, this has to be changed accordingly. However, in case of a parallel algorithm,
    it may be worth to pay the cost of creating more visitor objects. */
    data class Binary<T : Comparable<T>>(
        private var thatNode: BinaryDecisionDiagram<T>,
        private val operator: (first: Boolean, second: Boolean) -> Boolean,
        private val table: MutableMap<Int, BinaryDecisionDiagram<T>> = mutableMapOf()
    ) : ApplyBinaryDecisionDiagramVisitor<T>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>): BinaryDecisionDiagram<T> {
            return when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    val thatNodeValue = (thatNode as BinaryDecisionDiagram.Terminal).value
                    getResultUsingTable(BinaryDecisionDiagram.ofTerminal(operator(node.value, thatNodeValue)), table)
                }
                is BinaryDecisionDiagram.Variable -> {
                    val thatNodeAsVar = (thatNode as BinaryDecisionDiagram.Variable)
                    val thatNodeValue = thatNodeAsVar.value
                    thatNode = thatNodeAsVar.low
                    val lowNode = node.accept(this)
                    thatNode = thatNodeAsVar.high
                    val highNode = node.accept(this)
                    getResultUsingTable(createVarNode(thatNodeValue, lowNode, highNode), table)
                }
                else -> throw UnsupportedOperationException("Unknown BinaryDecisionDiagram instance")
            }
        }

        override fun visit(node: BinaryDecisionDiagram.Variable<T>): BinaryDecisionDiagram<T> {
            return when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    val curThatNode = thatNode
                    thatNode = node
                    curThatNode.accept(this)
                }
                is BinaryDecisionDiagram.Variable -> {
                    val thatNodeAsVar = (thatNode as BinaryDecisionDiagram.Variable)
                    val newValue: T
                    val firstLow: BinaryDecisionDiagram<T>
                    val firstHigh: BinaryDecisionDiagram<T>
                    val secondLow: BinaryDecisionDiagram<T>
                    val secondHigh: BinaryDecisionDiagram<T>

                    if (node.value <= thatNodeAsVar.value) {
                        newValue = node.value
                        firstHigh = node.high
                        firstLow = node.low
                    } else {
                        newValue = thatNodeAsVar.value
                        firstLow = node
                        firstHigh = node
                    }
                    if (thatNodeAsVar.value <= node.value) {
                        secondHigh = thatNodeAsVar.high
                        secondLow = thatNodeAsVar.low
                    } else {
                        secondLow = thatNode
                        secondHigh = thatNode
                    }

                    thatNode = secondLow
                    val lowNode = firstLow.accept(this)
                    thatNode = secondHigh
                    val highNode = firstHigh.accept(this)
                    getResultUsingTable(createVarNode(newValue, lowNode, highNode), table)
                }
                else -> throw UnsupportedOperationException("Unknown BinaryDecisionDiagram instance")
            }
        }
    }
}
