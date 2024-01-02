package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TermSerializer : Serializer<Term> {
    companion object {
        @JvmStatic
        @JsName("of")
        fun of(mimeType: MimeType): TermSerializer {
            return termSerializer(mimeType)
        }
    }
}
