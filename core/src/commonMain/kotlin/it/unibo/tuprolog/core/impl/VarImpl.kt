package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

internal class VarImpl(override val name: String, private val identifier: Int = instanceCount++) : TermImpl(), Var {

    companion object {
        private var instanceCount = 0
    }

    override val completeName: String by lazy {
        "${name}__$identifier"
    }

    override val isAnonymous: Boolean = super.isAnonymous

    override val isNameWellFormed: Boolean by lazy {
        Var.VAR_REGEX_PATTERN.matches(name)
    }

    override fun strictlyEquals(other: Term): Boolean =
            other is VarImpl && other::class == this::class
                    && completeName == other.completeName

    override fun structurallyEquals(other: Term): Boolean = other is VarImpl

    override fun freshCopy(): Var = VarImpl(name)

    override fun toString(): String = if (isNameWellFormed) completeName else "¿$completeName?"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as VarImpl

        if (completeName != other.completeName) return false

        return true
    }

    override fun hashCode(): Int = completeName.hashCode()
}
