package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithOperators
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithPrologStandardLibrary
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithSubstitutions
import it.unibo.tuprolog.dsl.LogicProgrammingScopeWithVariables
import it.unibo.tuprolog.dsl.MinimalLogicProgrammingScope
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScopeImpl
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScopeWithTheories
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnification
import it.unibo.tuprolog.solve.SolverFactory
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
    LogicProgrammingScopeWithTheories<LogicProgrammingScope>,
    LogicProgrammingScopeWithResolution<LogicProgrammingScope> {
    companion object {
        internal fun SolverFactory.changeUnificatorIfNecessary(unificator: Unificator): SolverFactory =
            if (defaultUnificator === unificator) this else newBuilder().unificator(unificator).toFactory()

        @JsName("of")
        fun of(
            scope: Scope = Scope.empty(),
            termificator: Termificator = Termificator.default(scope),
            variablesProvider: VariablesProvider = VariablesProvider.of(scope),
            unificator: Unificator = Unificator.default,
            theoryFactory: TheoryFactory = IndexedTheoryFactory(unificator),
            solverFactory: SolverFactory,
        ): LogicProgrammingScope =
            LogicProgrammingScopeImpl(
                scope,
                if (termificator.scope === scope) termificator else termificator.copy(scope),
                if (variablesProvider.scope === scope) variablesProvider else variablesProvider.copy(scope),
                unificator,
                if (theoryFactory.unificator === unificator) theoryFactory else theoryFactory.copy(unificator),
                solverFactory.changeUnificatorIfNecessary(unificator),
            )

        @JsName("ofSolverFactory")
        fun of(
            solverFactory: SolverFactory,
            unificator: Unificator = solverFactory.defaultUnificator,
            scope: Scope = Scope.empty(),
            termificator: Termificator = Termificator.default(scope),
            variablesProvider: VariablesProvider = VariablesProvider.of(scope),
            theoryFactory: TheoryFactory = IndexedTheoryFactory(unificator),
        ) = of(scope, termificator, variablesProvider, unificator, theoryFactory, solverFactory)
    }
}
