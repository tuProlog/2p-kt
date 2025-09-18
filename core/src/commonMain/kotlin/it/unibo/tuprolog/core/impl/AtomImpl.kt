package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var

internal open class AtomImpl(
    override val functor: String,
    tags: Map<String, Any> = emptyMap(),
) : AbstractStruct(functor, emptyList(), tags),
    Atom {
    override val args: List<Term>
        get() = emptyList()

    override val isGround: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = emptySequence()

    override fun copyWithTags(tags: Map<String, Any>): Atom = AtomImpl(functor, tags)

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitAtom(this)
}
