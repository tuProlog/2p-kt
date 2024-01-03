package it.unibo.tuprolog.solve.libs.oop.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.solve.libs.oop.NullRef
import it.unibo.tuprolog.solve.libs.oop.ObjectRef

@Suppress("UNCHECKED_CAST")
internal object NullRefImpl : NullRef, Atom by Atom.of(ObjectRef.nameOf(null)) {
    override val isConstant: Boolean
        get() = true

    override fun asConstant(): Constant = this

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun <T : Term> `as`(): T? = this as? T

    override fun <T : Term> castTo(): T = this as T

    override fun apply(substitution: Substitution): Term = this

    override fun apply(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): Term = this

    override fun get(
        substitution: Substitution,
        vararg substitutions: Substitution,
    ): Term = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitAtom(this)
}
