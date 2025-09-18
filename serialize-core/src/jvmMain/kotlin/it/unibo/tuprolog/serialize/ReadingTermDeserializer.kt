package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface ReadingTermDeserializer :
    TermDeserializer,
    ReadingDeserializer<Term> {
    companion object {
        @JvmStatic
        fun of(mimeType: MimeType): ReadingTermDeserializer = JvmTermDeserializer(mimeType)
    }
}
