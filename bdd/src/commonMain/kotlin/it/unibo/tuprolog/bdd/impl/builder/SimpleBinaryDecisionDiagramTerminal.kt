package it.unibo.tuprolog.bdd.impl.builder

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram

/**
 * @author Jason Dellaluce
 */
internal data class SimpleBinaryDecisionDiagramTerminal<T : Comparable<T>>(
    override val truth: Boolean,
) : BinaryDecisionDiagram.Terminal<T> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SimpleBinaryDecisionDiagramTerminal<*>

        if (truth != other.truth) return false

        return true
    }

    override fun hashCode(): Int = truth.hashCode()
}
