package it.unibo.tuprolog.bdd.impl.builder

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramBuilder

/**
 * Implements a simple [BinaryDecisionDiagramBuilder] that does
 * not apply platform-specific or reduction optimizations. This
 * provides basic means to build represent BDDs, and keeps the entire
 * data structure in memory in the form of a directed graph.
 *
 * @author Jason Dellaluce
 * */
internal class SimpleBinaryDecisionDiagramBuilder<T : Comparable<T>> : BinaryDecisionDiagramBuilder<T> {
    override fun buildVariable(
        value: T,
        low: BinaryDecisionDiagram<T>,
        high: BinaryDecisionDiagram<T>,
    ): BinaryDecisionDiagram<T> =
        SimpleBinaryDecisionDiagramVariable(
            value,
            low,
            high,
        )

    override fun buildTerminal(truth: Boolean): BinaryDecisionDiagram<T> =
        SimpleBinaryDecisionDiagramTerminal(
            truth,
        )
}
