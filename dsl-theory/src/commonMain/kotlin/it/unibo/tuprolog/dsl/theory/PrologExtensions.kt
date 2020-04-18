@file:JvmName("PrologExtensions")
package it.unibo.tuprolog.dsl.theory

import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@JsName("prolog")
fun <R> prolog(function: PrologWithTheories.() -> R): R {
    return PrologWithTheories.empty().function()
}