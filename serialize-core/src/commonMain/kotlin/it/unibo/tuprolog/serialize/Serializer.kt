package it.unibo.tuprolog.serialize

import kotlin.js.JsName

/**
 * Converts values of type [T] into their textual representation in a given [mimeType] (e.g.
 * JSON, YAML, XML). This is the building block for scenarios such as persisting values to disk,
 * sending them over a network, or exchanging them with tools written in other languages.
 *
 * A typical implementation first turns each value into a plain object via an [Objectifier], then
 * hands that object to a format-specific writer (e.g. a Jackson `ObjectMapper` on the JVM).
 *
 * @param T the type of values this serializer can serialize.
 * @see Deserializer for the inverse operation.
 */
interface Serializer<T> {
    /** The textual format this serializer produces. */
    @JsName("mimeType")
    val mimeType: MimeType

    /**
     * Serializes a single [value] into its textual representation in [mimeType].
     *
     * @throws SerializationException if [value] cannot be represented in [mimeType].
     */
    @JsName("serialize")
    fun serialize(value: T): String

    /** Serializes several [values] into a single textual representation of all of them. */
    @JsName("serializeMany")
    fun serializeMany(vararg values: T): String = serializeMany(listOf(*values))

    /** Serializes several [values] into a single textual representation of all of them. */
    @JsName("serializeManyIterable")
    fun serializeMany(values: Iterable<T>): String

    /** Serializes several [values] into a single textual representation of all of them. */
    @JsName("serializeManySequence")
    fun serializeMany(values: Sequence<T>): String = serializeMany(values.asIterable())
}
