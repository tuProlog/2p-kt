package it.unibo.tuprolog.bdd.impl

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.BinaryDecisionDiagramVisitor

internal class BinaryDecisionDiagramTerminalImpl<T : Comparable<T>>(
    override val value: Boolean,
) : BinaryDecisionDiagram.Terminal<T> {

    override fun <E> accept(visitor: BinaryDecisionDiagramVisitor<T, E>): E = visitor.visit(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BinaryDecisionDiagramTerminalImpl<*>

        if (value != other.value) return false

        return true
    }

    private val cachedHashCode: Int by lazy {
        value.hashCode()
    }

    override fun hashCode(): Int = cachedHashCode
}
