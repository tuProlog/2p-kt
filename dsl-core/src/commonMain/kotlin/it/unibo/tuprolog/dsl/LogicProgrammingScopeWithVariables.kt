package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import kotlin.js.JsName

interface LogicProgrammingScopeWithVariables<S : LogicProgrammingScopeWithVariables<S>> :
    BaseLogicProgrammingScope<S>,
    VariablesProvider {
    @JsName("variablesProvider")
    val variablesProvider: VariablesProvider

    override fun copy(scope: Scope): S
}
