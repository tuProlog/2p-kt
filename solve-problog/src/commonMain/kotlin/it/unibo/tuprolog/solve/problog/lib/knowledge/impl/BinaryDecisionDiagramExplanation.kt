package it.unibo.tuprolog.solve.problog.lib.knowledge.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.andThenExpansion
import it.unibo.tuprolog.bdd.any
import it.unibo.tuprolog.bdd.expansion
import it.unibo.tuprolog.bdd.map
import it.unibo.tuprolog.bdd.notThenExpansion
import it.unibo.tuprolog.bdd.orThenExpansion
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm

/**
 * This class implements [ProbExplanation] by compiling knowledge into [BinaryDecisionDiagram]s.
 *
 * @author Jason Dellaluce
 * */
internal class BinaryDecisionDiagramExplanation(
    val diagram: BinaryDecisionDiagram<ProbTerm>,
    private val computedValue: ComputedValue = EMPTY_COMPUTED_VALUE,
) : ProbExplanation {
    internal data class ComputedValue(
        val probability: Double?,
    )

    companion object {
        internal val EMPTY_COMPUTED_VALUE = ComputedValue(null)
        internal val FALSE_COMPUTED_VALUE = ComputedValue(0.0)
        internal val TRUE_COMPUTED_VALUE = ComputedValue(1.0)
    }

    private fun getAsInternal(that: ProbExplanation): BinaryDecisionDiagramExplanation {
        if (that !is BinaryDecisionDiagramExplanation) {
            throw UnsupportedOperationException("that does not support Binary Decision Diagrams")
        }
        return that
    }

    private fun computeExpansion(
        value: ProbTerm,
        low: ComputedValue,
        high: ComputedValue,
    ): ComputedValue =
        ComputedValue(
            if (low.probability != null && high.probability != null) {
                value.probability * high.probability + (1.0 - value.probability) * low.probability
            } else {
                null
            },
        )

    private val cachedNot: ProbExplanation by lazy {
        val result =
            diagram.notThenExpansion(FALSE_COMPUTED_VALUE, TRUE_COMPUTED_VALUE) { node, low, high ->
                computeExpansion(node, low, high)
            }
        BinaryDecisionDiagramExplanation(result.first, result.second)
    }

    override fun not(): ProbExplanation = cachedNot

    override fun and(that: ProbExplanation): ProbExplanation {
        val result =
            diagram.andThenExpansion(getAsInternal(that).diagram, FALSE_COMPUTED_VALUE, TRUE_COMPUTED_VALUE) {
                node,
                low,
                high,
                ->
                computeExpansion(node, low, high)
            }
        return BinaryDecisionDiagramExplanation(result.first, result.second)
    }

    override fun or(that: ProbExplanation): ProbExplanation {
        val result =
            diagram.orThenExpansion(getAsInternal(that).diagram, FALSE_COMPUTED_VALUE, TRUE_COMPUTED_VALUE) {
                node,
                low,
                high,
                ->
                computeExpansion(node, low, high)
            }
        return BinaryDecisionDiagramExplanation(result.first, result.second)
    }

    override val probability: Double by lazy {
        computedValue.probability
            ?: diagram.expansion(0.0, 1.0) { node, low, high ->
                node.probability * high + (1.0 - node.probability) * low
            }
    }

    override val containsAnyNotGroundTerm: Boolean by lazy {
        diagram.any { !it.isGround }
    }

    override fun toString(): String = "bdd:${diagram.hashCode()}"

    override fun apply(transformation: (ProbTerm) -> ProbTerm): ProbExplanation =
        BinaryDecisionDiagramExplanation(
            diagram.map {
                transformation(it)
            },
        )
}
