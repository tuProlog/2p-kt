package it.unibo.tuprolog.serialize

import java.io.Reader
import java.io.StringReader

interface ReadingDeserializer<T> : Deserializer<T> {
    fun deserialize(reader: Reader): T

    override fun deserialize(string: String): T =
        StringReader(string).use {
            deserialize(it)
        }

    fun deserializeMany(reader: Reader): Iterable<T>

    override fun deserializeMany(string: String): Iterable<T> =
        StringReader(string).use {
            deserializeMany(it)
        }
}
