package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.andThenExpansion
import it.unibo.tuprolog.bdd.any
import it.unibo.tuprolog.bdd.expansion
import it.unibo.tuprolog.bdd.map
import it.unibo.tuprolog.bdd.notThenExpansion
import it.unibo.tuprolog.bdd.orThenExpansion
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm

/**
 * This class implements [ProbExplanation] by compiling knowledge into [BinaryDecisionDiagram]s.
 *
 * @author Jason Dellaluce
 * */
internal class BinaryDecisionDiagramExplanation(
    val diagram: BinaryDecisionDiagram<ProbTerm>,
    private val computedProbability: Double? = null,
) : ProbExplanation {

    private fun getAsInternal(that: ProbExplanation): BinaryDecisionDiagramExplanation {
        if (that !is BinaryDecisionDiagramExplanation) {
            throw UnsupportedOperationException("that does not support Binary Decision Diagrams")
        }
        return that
    }

    override fun not(): ProbExplanation {
        val result = diagram.notThenExpansion<ProbTerm, Double?>(
            0.0,
            1.0
        ) { node, low, high ->
            if (low != null && high != null) {
                node.probability * high + (1.0 - node.probability) * low
            } else null
        }
        return BinaryDecisionDiagramExplanation(result.first, result.second)
    }

    override fun and(that: ProbExplanation): ProbExplanation {
        val result = diagram.andThenExpansion<ProbTerm, Double?>(
            getAsInternal(that).diagram,
            0.0,
            1.0
        ) { node, low, high ->
            if (low != null && high != null) {
                node.probability * high + (1.0 - node.probability) * low
            } else null
        }
        return BinaryDecisionDiagramExplanation(result.first, result.second)
    }

    override fun or(that: ProbExplanation): ProbExplanation {
        val result = diagram.orThenExpansion<ProbTerm, Double?>(
            getAsInternal(that).diagram,
            0.0,
            1.0
        ) { node, low, high ->
            if (low != null && high != null) {
                node.probability * high + (1.0 - node.probability) * low
            } else null
        }
        return BinaryDecisionDiagramExplanation(result.first, result.second)
    }

    override val probability: Double by lazy {
        computedProbability
            ?: diagram.expansion(0.0, 1.0) { node, low, high ->
                node.probability * high + (1.0 - node.probability) * low
            }
    }

    override val containsAnyNotGroundTerm: Boolean by lazy {
        diagram.any { !it.isGround }
    }

    override fun containsAnyVariable(variables: Set<Var>): Boolean {
        return diagram.any { it.variables.any { v -> v in variables } }
    }

    override fun toString(): String {
        return "bdd:${diagram.hashCode()}"
    }

    override fun apply(transformation: (ProbTerm) -> ProbTerm): ProbExplanation {
        return BinaryDecisionDiagramExplanation(
            diagram.map {
                transformation(it)
            }
        )
    }
}
