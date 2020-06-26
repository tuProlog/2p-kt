package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import java.io.Writer

class JvmTermSerializer(override val mimeType: MimeType) : WritingTermSerializer {

    private val mapper = mimeType.objectMapper
    private val objectifier = JvmTermObjectifier()

    override fun serialize(value: Term, writer: Writer) {
        mapper.writeValue(writer, objectifier.objectify(value))
    }
}