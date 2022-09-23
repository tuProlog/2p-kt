package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.theory.Theory

abstract class AbstractLibrary : Library {

    override val operators: OperatorSet
        get() = OperatorSet.EMPTY

    override val theory: Theory
        get() = Theory.empty()

    override val primitives: Map<Signature, Primitive>
        get() = emptyMap()

    override val functions: Map<Signature, LogicFunction>
        get() = emptyMap()

    override fun toString(): String {
        return "${super.toString()}(" +
            "alias=" + alias +
            ", primitives=" + primitives.keys.joinToString(", ", "{", "}") {
            "'${it.name}'/${it.arity}"
        } + ", functions=" + functions.keys.joinToString(", ", "{", "}") {
            "'${it.name}'/${it.arity}"
        } + ", theory=" + theory.clauses.joinToString(". ", "{", "}") +
            ", operators=" + operators.joinToString(", ", "{", "}") {
            "'${it.functor}':${it.priority}:${it.specifier}"
        } + ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Library) return false

        if (alias != other.alias) return false
        if (operators != other.operators) return false
        if (theory != other.theory) return false
        if (primitives != other.primitives) return false
        if (functions != other.functions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alias.hashCode()
        result = 31 * result + operators.hashCode()
        result = 31 * result + theory.hashCode()
        result = 31 * result + primitives.hashCode()
        result = 31 * result + functions.hashCode()
        return result
    }
}
