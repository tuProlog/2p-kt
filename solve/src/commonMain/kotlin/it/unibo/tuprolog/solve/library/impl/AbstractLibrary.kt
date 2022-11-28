package it.unibo.tuprolog.solve.library.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.function.LogicFunction
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Primitive

abstract class AbstractLibrary : Library {

    override val operators: OperatorSet
        get() = OperatorSet.EMPTY

    override val clauses: List<Clause>
        get() = emptyList()

    override val primitives: Map<Signature, Primitive>
        get() = emptyMap()

    override val functions: Map<Signature, LogicFunction>
        get() = emptyMap()

    private val rulesSignatures: Set<Signature> by lazy {
        clauses.asSequence().filterIsInstance<Rule>().map { it.extractSignature() }.toSet()
    }

    override fun hasRule(signature: Signature): Boolean = signature in rulesSignatures

    override fun toString(): String {
        return "${super.toString()}(" +
            "alias=" + alias +
            ", primitives=" + primitives.keys.joinToString(", ", "{", "}") {
            "'${it.name}'/${it.arity}"
        } + ", functions=" + functions.keys.joinToString(", ", "{", "}") {
            "'${it.name}'/${it.arity}"
        } + ", theory=" + clauses.joinToString(". ", "{", "}") +
            ", operators=" + operators.joinToString(", ", "{", "}") {
            "'${it.functor}':${it.priority}:${it.specifier}"
        } + ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Library) return false

        if (alias != other.alias) return false
        if (operators != other.operators) return false
        if (clauses != other.clauses) return false
        if (primitives != other.primitives) return false
        if (functions != other.functions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alias.hashCode()
        result = 31 * result + operators.hashCode()
        result = 31 * result + clauses.hashCode()
        result = 31 * result + primitives.hashCode()
        result = 31 * result + functions.hashCode()
        return result
    }
}
