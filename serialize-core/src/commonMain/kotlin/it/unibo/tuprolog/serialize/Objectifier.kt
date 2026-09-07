package it.unibo.tuprolog.serialize

import kotlin.js.JsName

/**
 * Converts values of type [T] into plain, format-agnostic objects (e.g. maps, lists, strings,
 * numbers, booleans) that a generic data-binding library (such as Jackson on the JVM) can then
 * turn into JSON, YAML, XML, or any other concrete textual representation.
 *
 * This is the intermediate step a [Serializer] relies on: it first objectifies a value with an
 * [Objectifier], then hands the resulting plain object to a format-specific writer. Splitting
 * the two steps means the very same object model can be rendered in any supported [MimeType]
 * without duplicating the conversion logic for each format.
 *
 * @param T the type of values this objectifier can convert.
 * @see Deobjectifier for the inverse conversion.
 */
interface Objectifier<T> {
    /** Converts a single [value] into a plain object. */
    @JsName("objectify")
    fun objectify(value: T): Any

    /** Converts several [values] into a single plain object representing all of them. */
    @JsName("objectifyMany")
    fun objectifyMany(vararg values: T): Any = objectifyMany(listOf(*values))

    /** Converts several [values] into a single plain object representing all of them. */
    @JsName("objectifyManyIterable")
    fun objectifyMany(values: Iterable<T>): Any

    /** Converts several [values] into a single plain object representing all of them. */
    @JsName("objectifyManySequence")
    fun objectifyMany(values: Sequence<T>): Any = objectifyMany(values.asIterable())
}
