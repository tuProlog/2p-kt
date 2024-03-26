package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName

interface BaseLogicProgrammingScope<S : BaseLogicProgrammingScope<S>> : Scope, Termificator {
    @JsName("termificator")
    val termificator: Termificator

    @JsName("anyToTerm")
    fun Any.toTerm(): Term = termify(this)

    @JsName("newScope")
    fun newScope(): S
}
