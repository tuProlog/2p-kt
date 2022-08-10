@file:JvmName("PrologExtensions")

package it.unibo.tuprolog.dsl.theory

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("logicProgramming")
fun <R> logicProgramming(function: LogicProgrammingScopeWithTheories.() -> R): R {
    return LogicProgrammingScopeWithTheories.empty().function()
}

@JsName("lp")
fun <R> lp(function: LogicProgrammingScopeWithTheories.() -> R): R = logicProgramming(function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(function: LogicProgrammingScopeWithTheories.() -> R): R = logicProgramming(function)
