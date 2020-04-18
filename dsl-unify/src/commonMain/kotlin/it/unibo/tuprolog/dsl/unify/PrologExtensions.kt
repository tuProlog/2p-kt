@file:JvmName("PrologExtensions")
package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@JsName("scope")
fun <R> PrologWithUnification.scope(function: PrologWithUnification.() -> R): R {
    return PrologWithUnification.empty().function()
}

@JsName("rule")
fun PrologWithUnification.rule(function: PrologWithUnification.() -> Term): Rule {
    return PrologWithUnification.empty().function() as Rule
}

@JsName("fact")
fun PrologWithUnification.fact(function: PrologWithUnification.() -> Term): Fact {
    return factOf(PrologWithUnification.empty().function() as Struct)
}

@JsName("prolog")
fun <R> prolog(function: PrologWithUnification.() -> R): R {
    return PrologWithUnification.empty().function()
}