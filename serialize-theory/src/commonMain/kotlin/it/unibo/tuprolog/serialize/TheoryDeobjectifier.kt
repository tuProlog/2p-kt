package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface TheoryDeobjectifier : Deobjectifier<Theory> {
    companion object {
        @JsName("default")
        @JvmStatic
        val default: TheoryDeobjectifier
            get() = theoryDeobjectifier()
    }
}