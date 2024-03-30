package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Termificator {
    @JsName("scope")
    val scope: Scope

    @JsName("toTerm")
    fun termify(value: Any?): Term

    @JsName("copy")
    fun copy(scope: Scope = this.scope): Termificator

    companion object {
        @JvmStatic
        @JsName("default")
        fun default(scope: Scope): Termificator = DefaultTermificator(scope, true)

        @JvmStatic
        @JsName("legacy")
        fun legacy(scope: Scope): Termificator = DefaultTermificator(scope, false)
    }
}
