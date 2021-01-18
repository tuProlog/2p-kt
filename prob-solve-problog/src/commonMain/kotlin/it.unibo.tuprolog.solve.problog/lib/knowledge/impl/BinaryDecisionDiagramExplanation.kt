package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.and
import it.unibo.tuprolog.bdd.any
import it.unibo.tuprolog.bdd.expansion
import it.unibo.tuprolog.bdd.map
import it.unibo.tuprolog.bdd.not
import it.unibo.tuprolog.bdd.or
import it.unibo.tuprolog.bdd.toGraphvizString
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm

/**
 * This class implements [ProbExplanation] by compiling knowledge into [BinaryDecisionDiagram]s.
 *
 * @author Jason Dellaluce
 * */
internal class BinaryDecisionDiagramExplanation (
    val diagram: BinaryDecisionDiagram<ProbTerm>
): ProbExplanation {

    private fun getAsInternal(that: ProbExplanation): BinaryDecisionDiagramExplanation {
        if (that !is BinaryDecisionDiagramExplanation) {
            throw UnsupportedOperationException("that does not support Binary Decision Diagrams")
        }
        return that
    }

    override fun not(): ProbExplanation {
        return BinaryDecisionDiagramExplanation(diagram.not())
    }

    override fun and(that: ProbExplanation): ProbExplanation {
        return BinaryDecisionDiagramExplanation(this.diagram and getAsInternal(that).diagram)
    }

    override fun or(that: ProbExplanation): ProbExplanation {
        return BinaryDecisionDiagramExplanation(this.diagram or getAsInternal(that).diagram)
    }

    override val probability: Double by lazy {
        diagram.expansion(0.0, 1.0) {
                node, low, high ->
            node.probability * high + (1.0 - node.probability) * low
        }
    }

    override val containsNonGroundTerm: Boolean by lazy {
        diagram.any { !it.isGround }
    }

    override fun toString(): String {
        return "bdd:${diagram.hashCode()}"
    }

    override fun apply(transformation: (ProbTerm) -> ProbTerm): ProbExplanation {
        return BinaryDecisionDiagramExplanation(diagram.map {
            transformation(it)
        })
    }
}

/**
 * Formats a the underlying data structure using Graphviz notation (https://graphviz.org/).
 * This provides a fast and widely supported solution to visualize the contents of a graph-like data structures.
 *
 * Non-graph data structure implementations of [ProbExplanation] will cause an exception to be thrown.
 */
internal fun ProbExplanation.formatToGraphviz(): String {
    if (this is BinaryDecisionDiagramExplanation) {
        return this.diagram.toGraphvizString()
    }
    throw UnsupportedOperationException("Graphviz formatting is only supported for graph-like data structures.")
}
