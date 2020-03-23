package it.unibo.tuprolog.solve.library.stdlib.magic

import it.unibo.tuprolog.core.*

object MagicCut : Atom by Atom.of("!") {

    const val FUNCTOR: String = "!MagicCut!"

    override val isConstant: Boolean
        get() = super<Atom>.isConstant

    override fun toString(): String {
        return FUNCTOR // different symbol for debugging purposes
    }

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun apply(substitution: Substitution): Term = this

    override fun get(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visit(this)
}