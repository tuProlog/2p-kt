package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import java.io.Reader

internal class JvmTheoryDeserializer(
    override val mimeType: MimeType,
) : ReadingTheoryDeserializer {
    private val mapper = mimeType.objectMapper

    override fun deserialize(reader: Reader): Theory =
        JvmTheoryDeobjectifier().deobjectify(
            mapper.readValue(reader, java.lang.Object::class.java),
        )

    override fun deserializeMany(reader: Reader): Iterable<Theory> =
        JvmTheoryDeobjectifier().deobjectifyMany(
            mapper.readValue(reader, java.lang.Object::class.java),
        )
}
