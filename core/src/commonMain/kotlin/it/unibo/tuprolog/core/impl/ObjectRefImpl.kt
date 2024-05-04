package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.TermVisitor

internal class ObjectRefImpl(value: Any, tags: Map<String, Any> = emptyMap()) : AbstractRef(value, tags), ObjectRef {
    override val value: Any
        get() = _value ?: error("BUG: object reference with null value")

    override fun copyWithDifferentTags(tags: Map<String, Any>): ObjectRefImpl = ObjectRefImpl(value, tags)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitObjectRef(this)
}
