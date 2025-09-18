package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import java.io.Reader

internal class JvmTermDeserializer(
    override val mimeType: MimeType,
) : ReadingTermDeserializer {
    private val mapper = mimeType.objectMapper

    override fun deserialize(reader: Reader): Term =
        JvmTermDeobjectifier().deobjectify(
            mapper.readValue(reader, java.lang.Object::class.java),
        )

    override fun deserializeMany(reader: Reader): Iterable<Term> =
        JvmTermDeobjectifier().deobjectifyMany(
            mapper.readValue(reader, java.lang.Object::class.java),
        )
}
