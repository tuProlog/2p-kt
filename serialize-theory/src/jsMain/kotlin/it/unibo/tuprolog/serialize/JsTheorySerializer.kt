package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

internal class JsTheorySerializer(
    override val mimeType: MimeType,
) : TheorySerializer {
    private val objectifier = JsTheoryObjectifier()

    override fun serialize(value: Theory): String = stringify(objectifier.objectify(value))

    override fun serializeMany(values: Iterable<Theory>): String = stringify(objectifier.objectifyMany(values))

    private fun stringify(objectified: Any): String =
        when (mimeType) {
            is MimeType.Xml -> throw NotImplementedError("XML is currently not supported in JS")
            is MimeType.Json -> JSON.stringify(objectified)
            is MimeType.Yaml -> YAML.stringify(objectified)
        }
}
