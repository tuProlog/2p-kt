package it.unibo.tuprolog.solve.stdlib.magic

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

object MagicCut : Atom by Atom.of("!") {

    const val FUNCTOR: String = "!MagicCut!"

    override val isConstant: Boolean
        get() = true

    override fun asConstant(): Constant = this

    override fun toString(): String = FUNCTOR // different symbol for debugging purposes

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun apply(substitution: Substitution): Term = this

    override fun get(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun apply(substitution: Substitution, vararg substitutions: Substitution): Term = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitAtom(this)
}
