package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

class JsTermDeserializer(override val mimeType: MimeType) : TermDeserializer {
    override fun deserialize(string: String): Term {
        val parsed = when (mimeType) {
            is MimeType.Xml -> throw NotImplementedError("XML is currently not supported in JS")
            is MimeType.Json -> JSON.parse(string)
            is MimeType.Yaml -> YAML.parse(string)
        }
        return JsTermDeobjectifier().deobjectify(parsed)
    }
}