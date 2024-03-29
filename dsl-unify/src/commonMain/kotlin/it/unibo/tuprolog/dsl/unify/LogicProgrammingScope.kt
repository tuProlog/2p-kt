package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithOperators
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithPrologStandardLibrary
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithSubstitutions
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithVariables
import it.unibo.tuprolog.dsl.MinimalLogicProgrammingScope
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface LogicProgrammingScope :
    MinimalLogicProgrammingScope<LogicProgrammingScope>,
    LogicProgrammingScopeWithSubstitutions<LogicProgrammingScope>,
    LogicProgrammingScopeWithPrologStandardLibrary<LogicProgrammingScope>,
    LogicProgrammingScopeWithOperators<LogicProgrammingScope>,
    LogicProgrammingScopeWithVariables<LogicProgrammingScope>,
    LogicProgrammingScopeWithUnification<LogicProgrammingScope> {
    companion object {
        internal val defaultUnificator = Unificator.default

        @JsName("empty")
        fun empty(): LogicProgrammingScope = of()

        @JsName("of")
        fun of(
            scope: Scope = Scope.empty(),
            termificatorFactory: (Scope) -> Termificator = { Termificator.default(it) },
            variablesProviderFactory: (Scope) -> VariablesProvider = { VariablesProvider.of(it) },
            unificatorFactory: (Scope) -> Unificator = { defaultUnificator },
        ): LogicProgrammingScope =
            LogicProgrammingScopeImpl(
                scope,
                termificatorFactory,
                variablesProviderFactory,
                unificatorFactory,
            )

        @JsName("ofUnificator")
        fun of(
            unificator: Unificator,
            scope: Scope = Scope.empty(),
            termificatorFactory: (Scope) -> Termificator = { Termificator.default(it) },
            variablesProviderFactory: (Scope) -> VariablesProvider = { VariablesProvider.of(it) },
        ): LogicProgrammingScope =
            of(
                scope,
                termificatorFactory,
                variablesProviderFactory,
            ) { unificator }
    }
}
