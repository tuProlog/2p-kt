package it.unibo.tuprolog.serialize

import java.io.StringWriter
import java.io.Writer

/**
 * A JVM-only [Serializer] that can also write directly to a [Writer] (e.g. a file or network
 * stream), avoiding the need to first build the whole output as a `String` in memory. The
 * `String`-returning [serialize]/[serializeMany] methods of [Serializer] are implemented in
 * terms of the [Writer]-based ones, by writing into a [StringWriter].
 *
 * @param T the type of values this serializer can serialize.
 */
interface WritingSerializer<T> : Serializer<T> {
    /** Serializes a single [value], writing its textual representation to [writer]. */
    fun serialize(
        writer: Writer,
        value: T,
    )

    override fun serialize(value: T): String =
        StringWriter().use {
            serialize(it, value)
            it.toString()
        }

    /** Serializes several [values], writing their textual representation to [writer]. */
    @Suppress("SpreadOperator")
    fun serializeMany(
        writer: Writer,
        vararg values: T,
    ) = serializeMany(writer, listOf(*values))

    /** Serializes several [values], writing their textual representation to [writer]. */
    fun serializeMany(
        writer: Writer,
        values: Iterable<T>,
    )

    override fun serializeMany(values: Iterable<T>): String =
        StringWriter().use {
            serializeMany(it, values)
            it.toString()
        }

    /** Serializes several [values], writing their textual representation to [writer]. */
    fun serializeMany(
        writer: Writer,
        values: Sequence<T>,
    ) = serializeMany(writer, values.asIterable())
}
