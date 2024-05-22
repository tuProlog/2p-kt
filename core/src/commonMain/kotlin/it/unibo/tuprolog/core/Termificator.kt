package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.DefaultTermificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

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

        @JvmStatic
        @JsName("empty")
        fun Any.raiseErrorConvertingTo(`class`: KClass<*>): Nothing =
            throw IllegalArgumentException("Cannot convert ${this::class} into $`class`")
    }
}
