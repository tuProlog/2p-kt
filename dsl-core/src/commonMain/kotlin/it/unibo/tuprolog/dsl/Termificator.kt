package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Termificator {
    @JsName("toTerm")
    fun toTerm(value: Any): Term

    companion object {
        @JvmStatic
        @JsName("default")
        fun default(scope: Scope): Termificator = custom(scope) {
            legacyConfiguration()
        }

        @JvmStatic
        @JsName("custom")
        fun custom(scope: Scope, builder: AbstractTermificator.() -> Unit): Termificator =
            DefaultTermificator(scope).also(builder)
    }
}
