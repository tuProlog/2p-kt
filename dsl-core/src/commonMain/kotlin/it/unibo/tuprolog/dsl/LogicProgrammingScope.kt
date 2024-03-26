package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import kotlin.js.JsName

interface LogicProgrammingScope :
    MinimalLogicProgrammingScope<LogicProgrammingScope>,
    LogicProgrammingScopeWithSubstitutions<LogicProgrammingScope>,
    LogicProgrammingScopeWithPrologStandardLibrary<LogicProgrammingScope>,
    LogicProgrammingScopeWithOperators<LogicProgrammingScope> {
    companion object {
        @JsName("empty")
        fun empty(): LogicProgrammingScope = of()

        @JsName("of")
        fun of(
            scope: Scope = Scope.empty(),
            termificatorFactory: (Scope) -> Termificator = { Termificator.default(it) },
            variablesProviderFactory: (Scope) -> VariablesProvider = { VariablesProvider.of(it) },
        ): LogicProgrammingScope = LogicProgrammingScopeImpl(scope, termificatorFactory, variablesProviderFactory)
    }
}
