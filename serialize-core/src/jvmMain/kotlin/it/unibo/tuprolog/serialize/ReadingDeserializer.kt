package it.unibo.tuprolog.serialize

import java.io.Reader
import java.io.StringReader

interface ReadingDeserializer<T> : Deserializer<T> {
    fun deserialize(reader: Reader): T

    override fun deserialize(string: String): T =
        StringReader(string).use {
            deserialize(it)
        }
}