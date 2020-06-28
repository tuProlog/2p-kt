package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term

interface WritingTermSerializer : TermSerializer, WritingSerializer<Term> {
    companion object {
        @JvmStatic
        fun of(mimeType: MimeType): WritingTermSerializer {
            return JvmTermSerializer(mimeType)
        }
    }
}