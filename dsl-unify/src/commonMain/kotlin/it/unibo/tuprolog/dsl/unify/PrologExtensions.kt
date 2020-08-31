@file:JvmName("PrologExtensions")
package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("scope")
fun <R> PrologScopeWithUnification.scope(function: PrologScopeWithUnification.() -> R): R {
    return PrologScopeWithUnification.empty().function()
}

@JsName("rule")
fun PrologScopeWithUnification.rule(function: PrologScopeWithUnification.() -> Term): Rule {
    return PrologScopeWithUnification.empty().function() as Rule
}

@JsName("fact")
fun PrologScopeWithUnification.fact(function: PrologScopeWithUnification.() -> Term): Fact {
    return factOf(PrologScopeWithUnification.empty().function() as Struct)
}

@JsName("prolog")
fun <R> prolog(function: PrologScopeWithUnification.() -> R): R {
    return PrologScopeWithUnification.empty().function()
}