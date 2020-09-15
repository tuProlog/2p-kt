package it.unibo.tuprolog.serialize

import kotlin.js.JsName

sealed class MimeType(
    @JsName("type")
    val type: String,
    @JsName("subType")
    val subType: String
) {

    object Json : MimeType("application", "json")

    object Yaml : MimeType("application", "yaml")

    object Xml : MimeType("application", "xml")
}
