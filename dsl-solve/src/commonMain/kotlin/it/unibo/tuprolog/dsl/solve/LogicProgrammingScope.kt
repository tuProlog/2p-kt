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

/**
 * Full-fledged Prolog DSL scope: assembles every `dsl-core`/`dsl-unify`/`dsl-theory` mixin
 * ([MinimalLogicProgrammingScope] term/clause builders, [LogicProgrammingScopeWithSubstitutions],
 * [LogicProgrammingScopeWithPrologStandardLibrary], [LogicProgrammingScopeWithOperators],
 * [LogicProgrammingScopeWithVariables], [LogicProgrammingScopeWithUnification],
 * [it.unibo.tuprolog.dsl.theory.LogicProgrammingScopeWithTheories]) plus [LogicProgrammingScopeWithResolution], the
 * resolution-driving sugar added by this module (`solve`/`staticKb`/`dynamicKb`, and every
 * [it.unibo.tuprolog.solve.MutableSolver] operation). This is the receiver type of [logicProgramming]/[prolog]:
 * ```kotlin
 * prolog {
 *     staticKb(fact { "parent"("abraham", "isaac") })
 *     solve("parent"("abraham", "X")).forEach { println(it) }
 * }
 * ```
 * Obtain an instance via [logicProgramming]/[lp]/[prolog] (typical client code) or directly via [of] (e.g. to reuse
 * an existing [Scope]/[Termificator]/[VariablesProvider]).
 */
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
        /** Returns this [SolverFactory] unchanged if [Unificator] [unificator] is already its default, otherwise rebuilds it (via [SolverFactory.newBuilder]) to use [unificator] instead. */
        internal fun SolverFactory.changeUnificatorIfNecessary(unificator: Unificator): SolverFactory =
            if (defaultUnificator === unificator) this else newBuilder().unificator(unificator).toFactory()

        /**
         * Builds a [LogicProgrammingScope] from its individual collaborators, defaulting most of them from [scope]
         * and [unificator]. [termificator], [variablesProvider] and [theoryFactory] are re-scoped/re-unified to
         * [scope]/[unificator] as needed if they don't already match; [solverFactory] is likewise rebuilt with
         * [unificator] if its own default differs.
         *
         * Most client code should go through [logicProgramming]/[lp]/[prolog] instead; use this overload when an
         * existing [Scope] (e.g. one shared with other DSL code) needs to be reused rather than started fresh.
         *
         * @param solverFactory the [SolverFactory] backing this scope's [it.unibo.tuprolog.solve.MutableSolver]
         * (see [LogicProgrammingScopeWithResolution.defaultSolver]).
         */
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

        /** Overload of [of] taking [solverFactory] first and defaulting [unificator] from it. */
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
