package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Deserializer<T> {
    @JsName("mimeType")
    val mimeType: MimeType

    @JsName("deserialize")
    fun deserialize(string: String): T

    @JsName("deserializeMany")
    fun deserializeMany(string: String): Iterable<T>
}