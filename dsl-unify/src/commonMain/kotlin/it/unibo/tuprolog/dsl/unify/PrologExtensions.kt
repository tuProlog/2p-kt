@file:JvmName("PrologExtensions")

package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("scope")
fun <R> LogicProgrammingScopeWithUnification.scope(function: LogicProgrammingScopeWithUnification.() -> R): R {
    return LogicProgrammingScopeWithUnification.empty().function()
}

@JsName("rule")
fun LogicProgrammingScopeWithUnification.rule(function: LogicProgrammingScopeWithUnification.() -> Term): Rule {
    return LogicProgrammingScopeWithUnification.empty().function() as Rule
}

@JsName("fact")
fun LogicProgrammingScopeWithUnification.fact(function: LogicProgrammingScopeWithUnification.() -> Term): Fact {
    return factOf(LogicProgrammingScopeWithUnification.empty().function() as Struct)
}

@JsName("logicProgramming")
fun <R> logicProgramming(function: LogicProgrammingScopeWithUnification.() -> R): R {
    return LogicProgrammingScopeWithUnification.empty().function()
}

@JsName("lp")
fun <R> lp(function: LogicProgrammingScopeWithUnification.() -> R): R = logicProgramming(function)

@Deprecated("Use `lp` or `logicProgramming` instead", ReplaceWith("lp(function)"))
@JsName("prolog")
fun <R> prolog(function: LogicProgrammingScopeWithUnification.() -> R): R = logicProgramming(function)
