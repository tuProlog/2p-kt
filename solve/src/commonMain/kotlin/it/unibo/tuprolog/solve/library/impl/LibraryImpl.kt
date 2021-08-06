package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory

/**
 * Default implementation class of [Library]
 *
 * @author Enrico
 */
open class LibraryImpl(
    override val operators: OperatorSet,
    override val theory: Theory,
    override val primitives: Map<Signature, Primitive>,
    override val functions: Map<Signature, LogicFunction>
) : Library {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LibraryImpl

        if (operators != other.operators) return false
        if (theory != other.theory) return false
        if (primitives != other.primitives) return false
        if (functions != other.functions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operators.hashCode()
        result = 31 * result + theory.hashCode()
        result = 31 * result + primitives.hashCode()
        result = 31 * result + functions.hashCode()
        return result
    }

    override fun toString(): String =
        "Library(operators=$operators, theory=$theory, primitives=$primitives, functions=$functions)"
}
