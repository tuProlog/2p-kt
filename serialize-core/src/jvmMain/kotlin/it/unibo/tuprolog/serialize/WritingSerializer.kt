package it.unibo.tuprolog.serialize

import java.io.StringWriter
import java.io.Writer

interface WritingSerializer<T> : Serializer<T> {
    fun serialize(
        writer: Writer,
        value: T,
    )

    override fun serialize(value: T): String =
        StringWriter().use {
            serialize(it, value)
            it.toString()
        }

    fun serializeMany(
        writer: Writer,
        vararg values: T,
    ) = serializeMany(writer, listOf(*values))

    fun serializeMany(
        writer: Writer,
        values: Iterable<T>,
    )

    override fun serializeMany(values: Iterable<T>): String =
        StringWriter().use {
            serializeMany(it, values)
            it.toString()
        }

    fun serializeMany(
        writer: Writer,
        values: Sequence<T>,
    ) = serializeMany(writer, values.asIterable())
}
