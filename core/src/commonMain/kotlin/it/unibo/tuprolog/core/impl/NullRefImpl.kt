package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

@Suppress("EqualsOrHashCode")
internal class NullRefImpl(
    tags: Map<String, Any> = emptyMap(),
) : TermImpl(tags),
    NullRef {
    override val hashCodeCache: Int
        get() = HASH

    override fun equals(other: Any?): Boolean = asTerm(other)?.isNullRef ?: false

    override fun equals(
        other: Term,
        useVarCompleteName: Boolean,
    ): Boolean = other.isNullRef

    override fun structurallyEquals(other: Term): Boolean = other.isNullRef

    override fun copyWithTags(tags: Map<String, Any>): NullRef = NullRefImpl(tags)

    override fun freshCopy(): NullRef = this

    override fun freshCopy(scope: Scope): NullRef = this

    override fun toString(): String = "<null-ref>"

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitNullRef(this)

    private companion object {
        // the bytes of "null"
        const val HASH = 0x6E756C6C
    }
}
