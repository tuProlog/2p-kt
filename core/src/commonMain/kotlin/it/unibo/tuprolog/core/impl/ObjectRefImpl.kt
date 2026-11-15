package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.ObjectRef

internal class ObjectRefImpl(
    value: Any,
    tags: Map<String, Any> = emptyMap(),
) : ObjectRef(value, tags) {
    override fun copyWithTags(tags: Map<String, Any>): ObjectRef = ObjectRefImpl(value, tags)
}
