package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

internal class BinaryDecisionDiagramVariableImpl<T : Comparable<T>>(
    override val value: T,
    override val low: BinaryDecisionDiagram<T>,
    override val high: BinaryDecisionDiagram<T>,
) : BinaryDecisionDiagram.Variable<T> {

    override fun <E> accept(visitor: BinaryDecisionDiagramVisitor<T, E>): E = visitor.visit(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BinaryDecisionDiagramVariableImpl<*>

        if (value != other.value) return false
        if (low != other.low) return false
        if (high != other.high) return false

        return true
    }

    private val cachedHashCode: Int by lazy {
        var result = value.hashCode()
        result = 31 * result + low.hashCode()
        result = 31 * result + high.hashCode()
        result
    }

    override fun hashCode(): Int = cachedHashCode
}
