package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Deserializer<T> {
    @JsName("mimeType")
    val mimeType: MimeType
    @JsName("deserialize")
    fun deserialize(string: String): T
    @JsName("reconstruct")
    fun reconstruct(`object`: Any): T
}