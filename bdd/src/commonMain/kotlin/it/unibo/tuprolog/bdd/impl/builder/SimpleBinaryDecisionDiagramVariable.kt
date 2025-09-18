package it.unibo.tuprolog.bdd.impl.builder

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram

/**
 * @author Jason Dellaluce
 */
internal data class SimpleBinaryDecisionDiagramVariable<T : Comparable<T>>(
    override val value: T,
    override val low: BinaryDecisionDiagram<T>,
    override val high: BinaryDecisionDiagram<T>,
) : BinaryDecisionDiagram.Variable<T> {
    private val hashCodeCache: Int by lazy {
        var result = value.hashCode()
        result = 31 * result + low.hashCode()
        result = 31 * result + high.hashCode()
        result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SimpleBinaryDecisionDiagramVariable<*>

        if (value != other.value) return false
        if (low != other.low) return false
        if (high != other.high) return false

        return true
    }

    override fun hashCode(): Int = hashCodeCache
}
