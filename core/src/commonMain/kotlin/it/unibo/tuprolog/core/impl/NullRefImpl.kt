package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.TermVisitor

@Suppress("EqualsOrHashCode")
internal class NullRefImpl(tags: Map<String, Any> = emptyMap()) : AbstractRef(null, tags), NullRef {
    override val value: Nothing
        get() = throw NullPointerException("Cannot get value of null reference")

    override fun copyWithDifferentTags(tags: Map<String, Any>): NullRefImpl = NullRefImpl(tags)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitNullRef(this)

    override fun freshCopy(): NullRef = this

    override fun freshCopy(scope: Scope): NullRef = this

    override fun equals(other: Any?): Boolean = asTerm(other)?.asNullRef()?.isNullRef ?: false
}
