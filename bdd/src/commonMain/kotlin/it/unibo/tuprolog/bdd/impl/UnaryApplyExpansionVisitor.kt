package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramBuilder
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

/**
 * This visitor implements the "Apply" [BinaryDecisionDiagram] construction
 * algorithm for **unary** Boolean operators, that produces Ordered Binary
 * Decision Diagrams (OBDD).
 *
 * This visitor is stateful, so it is not safe to share it across multiple
 * threads/coroutines, or to reuse it for subsequent operations.
 *
 * This is also optimized to perform a Shannon Expansion right ahead during
 * BDD construction. Since the "Apply" algorithm constructs diagrams bottom-up,
 * Shannon Expansion operations can be performed during the creation
 * of each node of the diagram. As such, methods of this visitor return an
 * instance of [Pair], of which [Pair.first] contains the
 * [BinaryDecisionDiagram] built with the "Apply" algorithm, and [Pair.second]
 * contains the result of Shannon Expansion computations. This is useful for
 * performing a computation over the resulting BDD by avoiding the need of
 * invoking `apply` and `expansion` subsequently.
 *
 * @author Jason Dellaluce
 */
internal class UnaryApplyExpansionVisitor<T : Comparable<T>, E>(
    private val builder: BinaryDecisionDiagramBuilder<T>,
    private val operator: (first: Boolean) -> Boolean,
    private val expansionFalseTerminal: E,
    private val expansionTrueTerminal: E,
    private val expansionOperator: (node: T, low: E, high: E) -> E
) : BinaryDecisionDiagramVisitor<T, ApplyExpansionResult<T, E>> {
    private val dynamicTable: MutableMap<Int, ApplyExpansionResult<T, E>> =
        mutableMapOf()

    override fun visit(
        node: BinaryDecisionDiagram.Terminal<T>
    ): ApplyExpansionResult<T, E> {
        val resTruth = operator(node.truth)
        return ApplyExpansionResult(
            builder.buildTerminal(resTruth),
            if (resTruth) expansionTrueTerminal else expansionFalseTerminal
        )
    }

    override fun visit(
        node: BinaryDecisionDiagram.Variable<T>
    ): ApplyExpansionResult<T, E> {
        val key = node.hashCode()
        if (dynamicTable.containsKey(key)) {
            return dynamicTable[key]!!
        }
        val lowNode = node.low.accept(this)
        val highNode = node.high.accept(this)
        val result = ApplyExpansionResult(
            builder.buildVariable(node.value, lowNode.first, highNode.first),
            expansionOperator(node.value, lowNode.second, highNode.second)
        )
        dynamicTable[key] = result
        return result
    }
}
