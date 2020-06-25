package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmStatic

interface TermSerializer : Serializer<Term> {

    companion object {
        @JvmStatic
        fun of(mimeType: MimeType): TermSerializer {
            return termSerializer(mimeType)
        }
    }

}