package it.unibo.tuprolog.libraries.stdlib.magic

import it.unibo.tuprolog.core.*

private val MAGIC_CUT_DELEGATE = Atom.of("!")

object MagicCut : Atom by MAGIC_CUT_DELEGATE {
    override val isConstant: Boolean
        get() = super<Atom>.isConstant

    override fun equals(other: Any?): Boolean {
        return MAGIC_CUT_DELEGATE == other
    }

    override fun hashCode(): Int {
        return MAGIC_CUT_DELEGATE.hashCode()
    }

    override fun toString(): String {
        return "¡" // different symbol for debugging purposes
    }

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun apply(substitution: Substitution): Term = this

    override fun get(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visit(this)
}