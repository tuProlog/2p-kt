package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Var

internal open class ObjectRefImpl(override val value: Any, tags: Map<String, Any>) : AbstractTerm(tags), ObjectRef {
    override val hashCodeCache: Int
        get() = value.hashCode()

    override fun freshCopy(): ObjectRef = this

    override fun freshCopy(scope: Scope): ObjectRef = this

    override fun copyWithTags(tags: Map<String, Any>): Term =
        if (this.tags != tags) {
            ObjectRefImpl(value, tags)
        } else {
            this
        }

    override fun equals(other: Term, useVarCompleteName: Boolean): Boolean =
        this == other

    override fun equals(other: Any?): Boolean =
        asTerm(other)?.asObjectRef()?.let { value == it.value } ?: false

    override fun structurallyEquals(other: Term): Boolean = equals(other)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitObjectRef(this)

    override fun toString(): String =


    override val variables: Sequence<Var>
        get() = TODO("Not yet implemented")

}