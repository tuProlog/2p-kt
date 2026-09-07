package it.unibo.tuprolog.serialize

import kotlin.js.JsName

/**
 * Converts plain, format-agnostic objects (e.g. maps, lists, strings, numbers, booleans) —
 * as produced by parsing some concrete textual format (JSON, YAML, XML, ...) into a generic
 * object tree — back into values of type [T].
 *
 * This is the inverse of [Objectifier], and it is the step a [Deserializer] relies on after a
 * format-specific parser has already turned raw text into a plain object tree.
 *
 * @param T the type of values this deobjectifier can produce.
 * @see Objectifier for the inverse conversion.
 */
interface Deobjectifier<T> {
    /**
     * Converts a single plain [object] into a value of type [T].
     *
     * @throws DeobjectificationException if [object] does not have the shape expected for a [T].
     */
    @JsName("deobjectify")
    fun deobjectify(`object`: Any): T

    /**
     * Converts a plain [object] representing a collection of values into an [Iterable] of [T].
     *
     * @throws DeobjectificationException if [object] does not have the shape expected for a
     * collection of [T], or if any of its elements does not have the shape expected for a [T].
     */
    @JsName("deobjectifyMany")
    fun deobjectifyMany(`object`: Any): Iterable<T>
}
