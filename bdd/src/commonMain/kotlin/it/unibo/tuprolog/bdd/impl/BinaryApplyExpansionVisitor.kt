package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramBuilder
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor
import it.unibo.tuprolog.bdd.impl.utils.CastVisitor

internal typealias ApplyExpansionResult<T, E> = Pair<BinaryDecisionDiagram<T>, E>

/**
 * This visitor implements the "Apply" [BinaryDecisionDiagram] construction
 * algorithm for **binary** Boolean operators, that produces Ordered Binary
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
 * Under the hood, this visitor also uses casting optimization to distinguish
 * [BinaryDecisionDiagram.Variable] and [BinaryDecisionDiagram.Terminal] nodes.
 * This enhances performance on platform targets where type checking is not
 * optimized, such as JS.
 *
 * @author Jason Dellaluce
 */
internal class BinaryApplyExpansionVisitor<T : Comparable<T>, E>(
    private val builder: BinaryDecisionDiagramBuilder<T>,
    private var thatNode: BinaryDecisionDiagram<T>,
    private val operator: (first: Boolean, second: Boolean) -> Boolean,
    private val expansionFalseTerminal: E,
    private val expansionTrueTerminal: E,
    private val expansionOperator: (node: T, low: E, high: E) -> E,
) : BinaryDecisionDiagramVisitor<T, ApplyExpansionResult<T, E>> {
    private val dynamicTable:
        MutableMap<Int, MutableMap<Int, ApplyExpansionResult<T, E>>> =
        mutableMapOf()

    private val castVisitor = CastVisitor<T, ApplyExpansionResult<T, E>>()

    override fun visit(node: BinaryDecisionDiagram.Terminal<T>): ApplyExpansionResult<T, E> =
        visitWithTable(node, thatNode) {
            castVisitor.onTerminal = { that -> this.apply(node, that) }
            castVisitor.onVariable = { that -> this.apply(node, that) }
            thatNode.accept(castVisitor)
        }

    override fun visit(node: BinaryDecisionDiagram.Variable<T>): ApplyExpansionResult<T, E> =
        visitWithTable(node, thatNode) {
            castVisitor.onTerminal = { that -> this.apply(node, that) }
            castVisitor.onVariable = { that -> this.apply(node, that) }
            thatNode.accept(castVisitor)
        }

    private fun visitWithTable(
        first: BinaryDecisionDiagram<T>,
        second: BinaryDecisionDiagram<T>,
        computation: () -> ApplyExpansionResult<T, E>,
    ): ApplyExpansionResult<T, E> {
        val firstKey = first.hashCode()
        val secondKey = second.hashCode()
        if (!dynamicTable.containsKey(firstKey)) {
            dynamicTable[firstKey] = mutableMapOf()
        }

        val cached = dynamicTable[firstKey]!![secondKey]
        if (cached != null) {
            return cached
        }
        val result = computation()
        dynamicTable[firstKey]!![secondKey] = result
        return result
    }

    private fun apply(
        first: BinaryDecisionDiagram.Terminal<T>,
        second: BinaryDecisionDiagram.Terminal<T>,
    ): ApplyExpansionResult<T, E> {
        val truth = operator(first.truth, second.truth)
        return ApplyExpansionResult(
            builder.buildTerminal(truth),
            if (truth) expansionTrueTerminal else expansionFalseTerminal,
        )
    }

    private fun apply(
        first: BinaryDecisionDiagram.Terminal<T>,
        second: BinaryDecisionDiagram.Variable<T>,
    ): ApplyExpansionResult<T, E> {
        thatNode = second.low
        val lowNode = first.accept(this)
        thatNode = second.high
        val highNode = first.accept(this)
        return ApplyExpansionResult(
            builder.buildVariable(second.value, lowNode.first, highNode.first),
            expansionOperator(second.value, lowNode.second, highNode.second),
        )
    }

    private fun apply(
        first: BinaryDecisionDiagram.Variable<T>,
        second: BinaryDecisionDiagram.Terminal<T>,
    ): ApplyExpansionResult<T, E> {
        thatNode = first
        return second.accept(this)
    }

    private fun apply(
        first: BinaryDecisionDiagram.Variable<T>,
        second: BinaryDecisionDiagram.Variable<T>,
    ): ApplyExpansionResult<T, E> {
        val newValue: T
        val firstLow: BinaryDecisionDiagram<T>
        val firstHigh: BinaryDecisionDiagram<T>
        val secondLow: BinaryDecisionDiagram<T>
        val secondHigh: BinaryDecisionDiagram<T>

        if (first.value <= second.value) {
            newValue = first.value
            firstHigh = first.high
            firstLow = first.low
        } else {
            newValue = second.value
            firstLow = first
            firstHigh = first
        }
        if (second.value <= first.value) {
            secondHigh = second.high
            secondLow = second.low
        } else {
            secondLow = second
            secondHigh = second
        }

        thatNode = secondLow
        val lowNode = firstLow.accept(this)
        thatNode = secondHigh
        val highNode = firstHigh.accept(this)
        return ApplyExpansionResult(
            builder.buildVariable(newValue, lowNode.first, highNode.first),
            expansionOperator(newValue, lowNode.second, highNode.second),
        )
    }
}
