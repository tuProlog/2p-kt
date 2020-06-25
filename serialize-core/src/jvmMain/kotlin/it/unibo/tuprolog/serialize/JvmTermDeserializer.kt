package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import java.io.Reader

class JvmTermDeserializer(override val mimeType: MimeType) : TermReadingDeserializer {

    private val mapper = mimeType.objectMapper

    override fun deserialize(reader: Reader): Term =
        JvmTermDeobjectifier().deobjectify(
            mapper.readValue(reader, java.lang.Object::class.java)
        )
}