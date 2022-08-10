package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName

interface LogicProgrammingAwareScope : Scope {
    @JsName("anyToTerm")
    fun Any.toTerm(): Term
}
