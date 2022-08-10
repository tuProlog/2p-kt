@file:JvmName("DSL")

package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("logicProgramming")
fun <R> logicProgramming(
    unificator: Unificator = Unificator.default,
    function: LogicProgrammingScopeWithUnification.() -> R
): R = LogicProgrammingScopeWithUnification.of(unificator).function()

@JsName("lp")
fun <R> lp(
    unificator: Unificator = Unificator.default,
    function: LogicProgrammingScopeWithUnification.() -> R
): R = logicProgramming(unificator, function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(
    unificator: Unificator = Unificator.default,
    function: LogicProgrammingScopeWithUnification.() -> R
): R = logicProgramming(unificator, function)
