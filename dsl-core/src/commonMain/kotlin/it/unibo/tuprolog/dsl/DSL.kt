@file:JvmName("DSL")

package it.unibo.tuprolog.dsl

import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@JsName("logicProgramming")
fun <R> logicProgramming(function: LogicProgrammingScope.() -> R): R = LogicProgrammingScope.empty().function()

@JsName("lp")
fun <R> lp(function: LogicProgrammingScope.() -> R): R = logicProgramming(function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(function: LogicProgrammingScope.() -> R): R = logicProgramming(function)

/** Utility method to launch conversion failed errors */
internal fun Any.raiseErrorConvertingTo(`class`: KClass<*>): Nothing =
    throw IllegalArgumentException("Cannot convert ${this::class} into $`class`")
