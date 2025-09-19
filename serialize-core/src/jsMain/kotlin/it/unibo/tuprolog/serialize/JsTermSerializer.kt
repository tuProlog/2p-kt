package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

internal class JsTermSerializer(
    override val mimeType: MimeType,
) : TermSerializer {
    private val objectifier = JsTermObjectifier()

    override fun serialize(value: Term): String = stringify(objectifier.objectify(value))

    override fun serializeMany(values: Iterable<Term>): String = stringify(objectifier.objectifyMany(values))

    private fun stringify(objectified: Any): String =
        when (mimeType) {
            is MimeType.Xml -> throw NotImplementedError("XML is currently not supported in JS")
            is MimeType.Json -> JSON.stringify(objectified)
            is MimeType.Yaml -> YAML.stringify(objectified)
        }
}
