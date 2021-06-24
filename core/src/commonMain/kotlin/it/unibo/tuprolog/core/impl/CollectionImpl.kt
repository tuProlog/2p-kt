package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Collection
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.itemWiseEquals
import it.unibo.tuprolog.utils.itemWiseHashCode

internal abstract class CollectionImpl(
    functor: String,
    args: List<Term>,
    tags: Map<String, Any>
) : StructImpl(functor, args, tags), Collection {

    override val unfoldedList: List<Term> by lazy { dequeOf(unfoldedSequence) }

    override val unfoldedArray: Array<Term>
        get() = unfoldedList.toTypedArray()

    override val isGround: Boolean by lazy { unfoldedSequence.all { it.isGround } }

    override fun argsHashCode(): Int = itemWiseHashCode(unfoldedSequence)

    override val variables: Sequence<Var> by lazy { unfoldedSequence.flatMap { it.variables } }

    abstract override fun copyWithTags(tags: Map<String, Any>): Collection

    override fun freshCopy(): Collection = super.freshCopy().castToCollection()

    override fun freshCopy(scope: Scope): Collection = super.freshCopy(scope).castToCollection()

    override fun itemsAreStructurallyEqual(other: Struct): Boolean =
        other.asCollection()?.let {
            itemWiseEquals(unfoldedSequence, it.unfoldedSequence) { a, b ->
                a.structurallyEquals(b)
            }
        } ?: false

    override fun itemsAreEqual(other: Struct, useVarCompleteName: Boolean): Boolean =
        other.asCollection()?.let {
            itemWiseEquals(unfoldedSequence, it.unfoldedSequence) { a, b ->
                a.equals(b, useVarCompleteName)
            }
        } ?: false

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitCollection(this)
}
