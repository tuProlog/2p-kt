package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

class JsTermSerializer(override val mimeType: MimeType) : TermSerializer {

    private val objectifier = JsTermObjectifier()

    override fun serialize(value: Term): String {
        val objectified = objectifier.objectify(value)
        return when (mimeType) {
            is MimeType.Xml -> throw NotImplementedError("XML is currently not supported in JS")
            is MimeType.Json -> JSON.stringify(objectified)
            is MimeType.Yaml -> YAML.stringify(objectified)
        }
    }
}