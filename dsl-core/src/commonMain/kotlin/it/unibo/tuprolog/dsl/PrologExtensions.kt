@file:JvmName("PrologExtensions")

package it.unibo.tuprolog.dsl

import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@JsName("prolog")
fun <R> prolog(function: PrologScope.() -> R): R = PrologScope.empty().function()

/** Utility method to launch conversion failed errors */
internal fun Any.raiseErrorConvertingTo(`class`: KClass<*>): Nothing =
    throw IllegalArgumentException("Cannot convert ${this::class} into $`class`")
