package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

private typealias ResultValue<T, E> = Pair<BinaryDecisionDiagram<T>, E>

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
 * This is also optimized to perform a Shannon Expansion right ahead during BDD construction. Since the "Apply"
 * algorithm constructs diagrams bottom-up, Shannon Expansion operations can be performed during the creation
 * of each node of the diagram. As such, methods of this visitor return an instance of [Pair], of which [Pair.first]
 * contains the [BinaryDecisionDiagram] built with the "Apply" algorithm, and [Pair.second] contains the result
 * of Shannon Expansion computations.
 *
 * @author Jason Dellaluce
 */
internal sealed class ApplyThenExpansionBinaryDecisionDiagramVisitor<T : Comparable<T>, E> :
    BinaryDecisionDiagramVisitor<T, ResultValue<T, E>> {
    /* Reduction: This avoids duplicated nodes, both variables and terminals.
    * NOTE: This also calls hashCode(), which initializes the lazy value that
    * will then be cached for fast later access. This is fundamental for reaching
    * O(1) on hashCode function even though this data structure is recursive. */
    protected fun getResultUsingTable(
        value: ResultValue<T, E>,
        table: MutableMap<Int, ResultValue<T, E>>
    ): ResultValue<T, E> {
        val key = value.hashCode()
        val tableValue = table[key]
        return if (tableValue != null) {
            tableValue
        } else {
            table[key] = value
            value
        }
    }

    /* Reduction: This avoids redundant nodes.
    * This also performs the Shannon Expansion computation. */
    protected fun createVarNode(
        value: T,
        low: ResultValue<T, E>,
        high: ResultValue<T, E>,
        expansionOperator: (node: T, low: E, high: E) -> E,
    ): ResultValue<T, E> {
        /* NOTE: This check is faster than invoking equals, but it makes sense only
           because of the getResultUsingTable function, which guarantees nodes to
           have "unique" instances over the diagram. */
        return if (low === high) {
            low
        } else {
            ResultValue(
                BinaryDecisionDiagram.ofVariable(value, low.first, high.first),
                expansionOperator(value, low.second, high.second)
            )
        }
    }

    class Unary<T : Comparable<T>, E>(
        private val operator: (first: Boolean) -> Boolean,
        private val expansionFalseTerminal: E,
        private val expansionTrueTerminal: E,
        private val expansionOperator: (node: T, low: E, high: E) -> E,
        private val table: MutableMap<Int, ResultValue<T, E>> = mutableMapOf()

    ) : ApplyThenExpansionBinaryDecisionDiagramVisitor<T, E>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>): ResultValue<T, E> {
            val result = operator(node.value)
            return getResultUsingTable(
                ResultValue(
                    BinaryDecisionDiagram.ofTerminal(result),
                    if (result) expansionTrueTerminal else expansionFalseTerminal
                ),
                table
            )
        }

        override fun visit(node: BinaryDecisionDiagram.Variable<T>): ResultValue<T, E> {
            val lowNode = node.low.accept(this)
            val highNode = node.high.accept(this)
            return getResultUsingTable(
                createVarNode(node.value, lowNode, highNode, expansionOperator),
                table
            )
        }
    }

    /* NOTE: This implementation of the Binary visitor does not require the creation of other sub-visitors,
    however it is NOT thread-safe due to its operand parameters being configurable. If we ever decide to move
    to a parallelized optimization, this has to be changed accordingly. However, in case of a parallel algorithm,
    it may be worth to pay the cost of creating more visitor objects. */
    class Binary<T : Comparable<T>, E>(
        private var thatNode: BinaryDecisionDiagram<T>,
        private val operator: (first: Boolean, second: Boolean) -> Boolean,
        private val expansionFalseTerminal: E,
        private val expansionTrueTerminal: E,
        private val expansionOperator: (node: T, low: E, high: E) -> E,
        private val table: MutableMap<Int, ResultValue<T, E>> = mutableMapOf()
    ) : ApplyThenExpansionBinaryDecisionDiagramVisitor<T, E>() {
        override fun visit(node: BinaryDecisionDiagram.Terminal<T>): ResultValue<T, E> {
            return when (thatNode) {
                is BinaryDecisionDiagram.Terminal -> {
                    val thatNodeValue = (thatNode as BinaryDecisionDiagram.Terminal).value
                    val result = operator(node.value, thatNodeValue)
                    val bdd = BinaryDecisionDiagram.ofTerminal<T>(result)
                    getResultUsingTable(
                        ResultValue(bdd, if (result) expansionTrueTerminal else expansionFalseTerminal),
                        table
                    )
                }
                is BinaryDecisionDiagram.Variable -> {
                    val thatNodeAsVar = (thatNode as BinaryDecisionDiagram.Variable)
                    val thatNodeValue = thatNodeAsVar.value
                    thatNode = thatNodeAsVar.low
                    val lowNode = node.accept(this)
                    thatNode = thatNodeAsVar.high
                    val highNode = node.accept(this)
                    getResultUsingTable(
                        createVarNode(thatNodeValue, lowNode, highNode, expansionOperator),
                        table
                    )
                }
                else -> throw UnsupportedOperationException("Unsupported BinaryDecisionDiagram instance")
            }
        }

        override fun visit(node: BinaryDecisionDiagram.Variable<T>): ResultValue<T, E> {
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
                    getResultUsingTable(createVarNode(newValue, lowNode, highNode, expansionOperator), table)
                }
                else -> throw UnsupportedOperationException("Unsupported BinaryDecisionDiagram instance")
            }
        }
    }
}
