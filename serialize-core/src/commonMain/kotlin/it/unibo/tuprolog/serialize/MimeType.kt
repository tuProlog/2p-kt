package it.unibo.tuprolog.serialize

sealed class MimeType(val type: String, val subType: String) {

    object Json : MimeType("application", "json")

    object Yaml : MimeType("application", "yaml")

    object Xml : MimeType("application", "xml")

}