@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("logicProgramming")
fun <R> logicProgramming(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = LogicProgrammingScope.of(unificator).function()

@JsName("lp")
fun <R> lp(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(
    unificator: Unificator = LogicProgrammingScope.defaultUnificator,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)
