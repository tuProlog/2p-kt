package it.unibo.tuprolog.core

internal class TestObjectRef(
    value: Any,
    tags: Map<String, Any> = emptyMap(),
) : ObjectRef(value, tags) {
    override fun copyWithTags(tags: Map<String, Any>): ObjectRef = TestObjectRef(value, tags)
}
