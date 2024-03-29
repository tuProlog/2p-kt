@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.dsl.unify.LogicProgrammingScope
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("logicProgramming")
fun <R> logicProgramming(
    unificator: Unificator = Unificator.default,
    function: LogicProgrammingScopeWithTheories.() -> R,
): R {
    return LogicProgrammingScopeWithTheories.of(unificator).function()
}

@JsName("lp")
fun <R> lp(
    unificator: Unificator = Unificator.default,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(
    unificator: Unificator = Unificator.default,
    function: LogicProgrammingScope.() -> R,
): R = logicProgramming(unificator, function)
