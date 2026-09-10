@file:JvmName("DSL")

package it.unibo.tuprolog.dsl

import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

/**
 * Entry point of the Prolog term-construction DSL: runs [function] with a fresh, empty [LogicProgrammingScope]
 * as its receiver, and returns whatever [function] returns. This is the idiomatic way of building
 * [it.unibo.tuprolog.core.Term]s, [it.unibo.tuprolog.core.Clause]s and the like through the DSL, instead of
 * instantiating a [LogicProgrammingScope] by hand:
 * ```kotlin
 * val term = logicProgramming { "parent"("abraham", "isaac") } // Struct: parent(abraham, isaac)
 * ```
 */
@JsName("logicProgramming")
fun <R> logicProgramming(function: LogicProgrammingScope.() -> R): R = LogicProgrammingScope.empty().function()

/** Shorthand for [logicProgramming]. */
@JsName("lp")
fun <R> lp(function: LogicProgrammingScope.() -> R): R = logicProgramming(function)

/** Deprecated alias of [logicProgramming]/[lp]; kept only for backwards compatibility. */
@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(function: LogicProgrammingScope.() -> R): R = logicProgramming(function)

/** Utility method to launch conversion failed errors */
internal fun Any.raiseErrorConvertingTo(`class`: KClass<*>): Nothing =
    throw IllegalArgumentException("Cannot convert ${this::class} into $`class`")
