package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TheorySerializer : Serializer<Theory> {
    companion object {
        @JvmStatic
        @JsName("of")
        fun of(mimeType: MimeType): TheorySerializer = theorySerializer(mimeType)
    }
}
