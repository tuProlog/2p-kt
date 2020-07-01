package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Serializer<T> {
    @JsName("mimeType")
    val mimeType: MimeType

    @JsName("serialize")
    fun serialize(value: T): String

    @JsName("serializeMany")
    fun serializeMany(vararg values: T): String =
        serializeMany(listOf(*values))

    @JsName("serializeManyIterable")
    fun serializeMany(values: Iterable<T>): String

    @JsName("serializeManySequence")
    fun serializeMany(values: Sequence<T>): String =
        serializeMany(values.asIterable())
}