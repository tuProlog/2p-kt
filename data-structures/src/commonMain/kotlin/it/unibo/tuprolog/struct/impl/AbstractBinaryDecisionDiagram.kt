package it.unibo.tuprolog.struct.impl

import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.exception.DataStructureOperationException

/**
 * This is an abstract class to implement both Terminal and Variable nodes of a BDD.
 * The construction algorithm is supposed to produce an Ordered Binary Decision Diagram (OBDD).
 *
 * If a node is Terminal, then [value], [low], [high] must be set to null and [boolValue]
 * must be not null. The total opposite stands in case of a Variable node instead.
 *
 * This implementation has been based on and inspired by the following resources:
 *   - https://www.inf.unibz.it/~artale/FM/slide7.pdf
 *   - https://github.com/TUK-CPS/jAADD
 */
internal abstract class AbstractBinaryDecisionDiagram<T>(
        val index: Int,
        val boolValue: Boolean?,
        val value: T?,
        val low: AbstractBinaryDecisionDiagram<T>?,
        val high: AbstractBinaryDecisionDiagram<T>?,
): BinaryDecisionDiagram<T> {

    abstract val isTerminal: Boolean

    abstract fun apply(
            other: AbstractBinaryDecisionDiagram<T>,
            binaryOp: (first: Boolean, second: Boolean) -> Boolean
    ): AbstractBinaryDecisionDiagram<T>

    abstract fun apply(
            unaryOp: (first: Boolean) -> Boolean
    ) : AbstractBinaryDecisionDiagram<T>

    override fun not(): BinaryDecisionDiagram<T> {
        return apply() { a -> !a }
    }

    override fun and(other: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T> {
        return apply(otherAsInternalImpl(other)) { a, b -> a && b }
    }

    override fun or(other: BinaryDecisionDiagram<T>): BinaryDecisionDiagram<T> {
        return apply(otherAsInternalImpl(other)) { a, b -> a || b }
    }

    private fun otherAsInternalImpl(other: BinaryDecisionDiagram<T>): AbstractBinaryDecisionDiagram<T> {
        if (other !is AbstractBinaryDecisionDiagram)
            throw DataStructureOperationException("Operations over different BDD implementations are not supported yet")
        return other
    }
}