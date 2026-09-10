package it.unibo.tuprolog.serialize

import kotlin.js.JsName

/**
 * Converts a textual representation in a given [mimeType] (e.g. JSON, YAML, XML) back into
 * values of type [T]. This is the counterpart of [Serializer], used e.g. to load a value that
 * was previously persisted to disk or received over a network.
 *
 * A typical implementation first parses the input string into a plain object tree using a
 * format-specific reader (e.g. a Jackson `ObjectMapper` on the JVM), then converts that tree
 * into a [T] via a [Deobjectifier].
 *
 * @param T the type of values this deserializer can produce.
 * @see Serializer for the inverse operation.
 */
interface Deserializer<T> {
    /** The textual format this deserializer consumes. */
    @JsName("mimeType")
    val mimeType: MimeType

    /**
     * Deserializes a single value of type [T] from its textual representation [string].
     *
     * @throws DeobjectificationException if [string] parses correctly as [mimeType] but does
     * not encode a valid [T].
     * @throws RuntimeException (a format-specific parsing exception, e.g. from the underlying
     * Jackson `ObjectMapper` on the JVM) if [string] is not well-formed [mimeType] in the first
     * place.
     */
    @JsName("deserialize")
    fun deserialize(string: String): T

    /**
     * Deserializes several values of type [T] from a single textual representation [string]
     * of all of them.
     *
     * @throws DeobjectificationException if [string] parses correctly as [mimeType] but does
     * not encode a valid collection of [T].
     * @throws RuntimeException (a format-specific parsing exception, e.g. from the underlying
     * Jackson `ObjectMapper` on the JVM) if [string] is not well-formed [mimeType] in the first
     * place.
     */
    @JsName("deserializeMany")
    fun deserializeMany(string: String): Iterable<T>
}
