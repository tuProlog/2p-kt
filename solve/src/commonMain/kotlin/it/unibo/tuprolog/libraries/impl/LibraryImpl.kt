package it.unibo.tuprolog.libraries.impl

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Default implementation class of [Library]
 *
 * @author Enrico
 */
internal open class LibraryImpl(
        override val operators: OperatorSet,
        override val theory: ClauseDatabase,
        override val primitives: Map<Signature, Primitive>
) : Library {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LibraryImpl

        if (operators != other.operators) return false
        if (theory != other.theory) return false
        if (primitives != other.primitives) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operators.hashCode()
        result = 31 * result + theory.hashCode()
        result = 31 * result + primitives.hashCode()
        return result
    }
}
