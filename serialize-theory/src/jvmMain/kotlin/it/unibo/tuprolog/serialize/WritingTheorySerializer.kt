package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory

interface WritingTheorySerializer :
    TheorySerializer,
    WritingSerializer<Theory> {
    companion object {
        @JvmStatic
        fun of(mimeType: MimeType): WritingTheorySerializer = JvmTheorySerializer(mimeType)
    }
}
