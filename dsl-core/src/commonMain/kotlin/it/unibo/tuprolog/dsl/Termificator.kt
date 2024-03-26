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

    companion object {
        @JvmStatic
        @JsName("default")
        fun default(scope: Scope): Termificator =
            custom(scope) {
                legacyConfiguration()
            }

        @JvmStatic
        @JsName("novel")
        fun novel(scope: Scope): Termificator =
            custom(scope) {
                novelConfiguration()
            }

        @JvmStatic
        @JsName("custom")
        fun custom(
            scope: Scope,
            builder: AbstractTermificator.() -> Unit,
        ): Termificator = DefaultTermificator(scope).also(builder)
    }
}
