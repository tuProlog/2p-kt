package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

internal class JsTheoryDeserializer(
    override val mimeType: MimeType,
) : TheoryDeserializer {
    override fun deserialize(string: String): Theory = JsTheoryDeobjectifier().deobjectify(parse(string))

    override fun deserializeMany(string: String): Iterable<Theory> =
        JsTheoryDeobjectifier().deobjectifyMany(parse(string))

    private fun parse(string: String): Any =
        when (mimeType) {
            is MimeType.Xml -> throw NotImplementedError("XML is currently not supported in JS")
            is MimeType.Json -> JSON.parse(string)
            is MimeType.Yaml -> YAML.parse(string)
        } as Any
}
