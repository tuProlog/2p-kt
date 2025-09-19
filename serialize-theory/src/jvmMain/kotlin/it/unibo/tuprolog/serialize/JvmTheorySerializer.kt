package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import java.io.Writer

internal class JvmTheorySerializer(
    override val mimeType: MimeType,
) : WritingTheorySerializer {
    private val mapper = mimeType.objectMapper
    private val objectifier = JvmTheoryObjectifier()

    override fun serialize(
        writer: Writer,
        value: Theory,
    ) {
        mapper.writeValue(writer, objectifier.objectify(value))
    }

    override fun serializeMany(
        writer: Writer,
        values: Iterable<Theory>,
    ) {
        mapper.writeValue(writer, objectifier.objectifyMany(values))
    }
}
