package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithOperators
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithPrologStandardLibrary
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithSubstitutions
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithVariables
import it.unibo.tuprolog.dsl.MinimalLogicProgrammingScope
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnification
import it.unibo.tuprolog.theory.IndexedTheoryFactory
import it.unibo.tuprolog.theory.TheoryFactory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface LogicProgrammingScope :
    MinimalLogicProgrammingScope<LogicProgrammingScope>,
    LogicProgrammingScopeWithSubstitutions<LogicProgrammingScope>,
    LogicProgrammingScopeWithPrologStandardLibrary<LogicProgrammingScope>,
    LogicProgrammingScopeWithOperators<LogicProgrammingScope>,
    LogicProgrammingScopeWithVariables<LogicProgrammingScope>,
    LogicProgrammingScopeWithUnification<LogicProgrammingScope>,
    LogicProgrammingScopeWithTheories<LogicProgrammingScope> {
    companion object {
        internal val defaultUnificator = Unificator.default

        @JsName("empty")
        fun empty(): LogicProgrammingScope = of()

        @JsName("of")
        fun of(
            scope: Scope = Scope.empty(),
            termificatorFactory: (Scope) -> Termificator = { Termificator.default(it) },
            variablesProviderFactory: (Scope) -> VariablesProvider = { VariablesProvider.of(it) },
            unificator: Unificator = defaultUnificator,
            theoryFactoryFactory: (Unificator) -> TheoryFactory = { IndexedTheoryFactory(it) },
        ): LogicProgrammingScope =
            LogicProgrammingScopeImpl(
                scope,
                termificatorFactory,
                variablesProviderFactory,
                unificator,
                theoryFactoryFactory,
            )

        @JsName("ofUnificator")
        fun of(
            unificator: Unificator,
            scope: Scope = Scope.empty(),
            termificatorFactory: (Scope) -> Termificator = { Termificator.default(it) },
            variablesProviderFactory: (Scope) -> VariablesProvider = { VariablesProvider.of(it) },
            theoryFactoryFactory: (Unificator) -> TheoryFactory = { IndexedTheoryFactory(it) },
        ): LogicProgrammingScope =
            of(
                scope,
                termificatorFactory,
                variablesProviderFactory,
                unificator,
                theoryFactoryFactory,
            )
    }
}
