package it.unibo.tuprolog.serialize

import java.io.StringWriter
import java.io.Writer

interface WritingSerializer<T> : Serializer<T> {
    fun serialize(value: T, writer: Writer)

    override fun serialize(value: T): String =
        StringWriter().use {
            serialize(value, it)
            it.toString()
        }
}