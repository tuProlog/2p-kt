package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

internal class JsTermDeserializer(
    override val mimeType: MimeType,
) : TermDeserializer {
    override fun deserialize(string: String): Term = JsTermDeobjectifier().deobjectify(parse(string))

    override fun deserializeMany(string: String): Iterable<Term> = JsTermDeobjectifier().deobjectifyMany(parse(string))

    private fun parse(string: String): Any =
        when (mimeType) {
            is MimeType.Xml -> throw NotImplementedError("XML is currently not supported in JS")
            is MimeType.Json -> JSON.parse(string)
            is MimeType.Yaml -> YAML.parse(string)
        } as Any
}
