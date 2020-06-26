package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TermDeobjectifier<T> : Deobjectifier<Term, T> {
    companion object {
        @JsName("default")
        @JvmStatic
        val default: TermDeobjectifier<*>
            get() = termDeobjectifier()
    }
}