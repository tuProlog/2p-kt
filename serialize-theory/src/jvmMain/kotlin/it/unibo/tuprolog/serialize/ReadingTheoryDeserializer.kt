package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

interface ReadingTheoryDeserializer :
    TheoryDeserializer,
    ReadingDeserializer<Theory> {
    companion object {
        @JvmStatic
        fun of(mimeType: MimeType): ReadingTheoryDeserializer = JvmTheoryDeserializer(mimeType)
    }
}
