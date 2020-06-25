package it.unibo.tuprolog.serialize

import kotlin.js.JsName

interface Serializer<T> {
    @JsName("mimeType")
    val mimeType: MimeType
    @JsName("serialize")
    fun serialize(value: T): String
    @JsName("objectify")
    fun objectify(value: T): Any
}