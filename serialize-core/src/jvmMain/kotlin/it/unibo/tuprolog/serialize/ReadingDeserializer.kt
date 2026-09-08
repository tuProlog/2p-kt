package it.unibo.tuprolog.serialize

import java.io.Reader
import java.io.StringReader

/**
 * A JVM-only [Deserializer] that can also read directly from a [Reader] (e.g. a file or network
 * stream), avoiding the need to first buffer the whole input into a `String`. The `String`-based
 * [deserialize]/[deserializeMany] methods of [Deserializer] are implemented in terms of the
 * [Reader]-based ones, by wrapping the string in a [StringReader].
 *
 * @param T the type of values this deserializer can produce.
 */
interface ReadingDeserializer<T> : Deserializer<T> {
    /** Deserializes a single value of type [T] by reading its textual representation from [reader]. */
    fun deserialize(reader: Reader): T

    override fun deserialize(string: String): T =
        StringReader(string).use {
            deserialize(it)
        }

    /** Deserializes several values of type [T] by reading their textual representation from [reader]. */
    fun deserializeMany(reader: Reader): Iterable<T>

    override fun deserializeMany(string: String): Iterable<T> =
        StringReader(string).use {
            deserializeMany(it)
        }
}
