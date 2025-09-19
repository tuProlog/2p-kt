package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import java.io.Writer

internal class JvmTermSerializer(
    override val mimeType: MimeType,
) : WritingTermSerializer {
    private val mapper = mimeType.objectMapper
    private val objectifier = JvmTermObjectifier()

    override fun serialize(
        writer: Writer,
        value: Term,
    ) {
        mapper.writeValue(writer, objectifier.objectify(value))
    }

    override fun serializeMany(
        writer: Writer,
        values: Iterable<Term>,
    ) {
        mapper.writeValue(writer, objectifier.objectifyMany(values))
    }
}
