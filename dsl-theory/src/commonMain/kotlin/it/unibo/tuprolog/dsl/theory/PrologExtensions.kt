@file:JvmName("PrologExtensions")

package it.unibo.tuprolog.dsl.theory

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("prolog")
fun <R> prolog(function: PrologScopeWithTheories.() -> R): R {
    return PrologScopeWithTheories.empty().function()
}
