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

/**
 * Full term-construction, unification *and* theory-building scope of `:dsl-theory`'s Prolog DSL: composes
 * `:dsl-core`'s scope mixins ([MinimalLogicProgrammingScope], [LogicProgrammingScopeWithSubstitutions],
 * [LogicProgrammingScopeWithPrologStandardLibrary], [LogicProgrammingScopeWithOperators],
 * [LogicProgrammingScopeWithVariables]) and `:dsl-unify`'s [LogicProgrammingScopeWithUnification] with
 * [LogicProgrammingScopeWithTheories], so an [it.unibo.tuprolog.theory.Theory]/[it.unibo.tuprolog.theory.MutableTheory]
 * can be built directly from the DSL (via [it.unibo.tuprolog.dsl.theory.LogicProgrammingScopeWithTheories.theory]/
 * [it.unibo.tuprolog.dsl.theory.LogicProgrammingScopeWithTheories.mutableTheory]), all self-referentially typed so
 * chained calls keep resolving to this same, richer scope.
 *
 * [logicProgramming]/[lp] are the idiomatic way to obtain and use one, optionally choosing which [Unificator]
 * strategy governs it; [of] and [empty] exist for callers that need to plug in a custom [Termificator],
 * [VariablesProvider], [Unificator] or [TheoryFactory] (or build a scope without immediately entering it).
 * [it.unibo.tuprolog.dsl.LogicProgrammingScope.withTheories] and
 * [it.unibo.tuprolog.dsl.unify.LogicProgrammingScope.withTheories] upgrade an existing `:dsl-core`/`:dsl-unify`
 * scope into one of these.
 *
 * @see logicProgramming
 */
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

        /** Shorthand for [of] with every parameter defaulted, i.e. a scope backed by a brand-new, empty [Scope],
         * the [defaultUnificator] unification strategy and an [IndexedTheoryFactory] built on it. */
        @JsName("empty")
        fun empty(): LogicProgrammingScope = of()

        /**
         * Creates a new [LogicProgrammingScope] backed by [scope] (a fresh, empty one by default), using
         * [termificator] to convert plain values into [it.unibo.tuprolog.core.Term]s, [variablesProvider] to
         * create fresh variables, [unificator] (the [defaultUnificator] strategy unless stated otherwise) to
         * unify them, and [theoryFactory] (an [IndexedTheoryFactory] over [unificator] by default) to build
         * [it.unibo.tuprolog.theory.Theory]/[it.unibo.tuprolog.theory.MutableTheory] instances. If [termificator]
         * or [variablesProvider] is not already backed by [scope], it is [Termificator.copy]/[VariablesProvider.copy]-ed
         * onto it first; if [theoryFactory] is not already using [unificator], it is
         * [TheoryFactory.copy]-ed onto it first — so all four end up agreeing on the same [Scope]/[Unificator]
         * (a requirement of the returned scope's implementation).
         */
        @JsName("of")
        fun of(
            scope: Scope = Scope.empty(),
            termificator: Termificator = Termificator.default(scope),
            variablesProvider: VariablesProvider = VariablesProvider.of(scope),
            unificator: Unificator = defaultUnificator,
            theoryFactory: TheoryFactory = IndexedTheoryFactory(unificator),
        ): LogicProgrammingScope =
            LogicProgrammingScopeImpl(
                scope,
                if (termificator.scope === scope) termificator else termificator.copy(scope),
                if (variablesProvider.scope === scope) variablesProvider else variablesProvider.copy(scope),
                unificator,
                if (theoryFactory.unificator === unificator) theoryFactory else theoryFactory.copy(unificator),
            )

        /**
         * Overload of [of] taking [unificator] and [theoryFactory] as its first parameters (rather than last), so
         * a non-default [Unificator]/[TheoryFactory] can be supplied positionally without naming the argument — as
         * [logicProgramming] and [lp] do.
         */
        @JsName("ofUnificator")
        fun of(
            unificator: Unificator,
            theoryFactory: TheoryFactory = IndexedTheoryFactory(unificator),
            scope: Scope = Scope.empty(),
            termificator: Termificator = Termificator.default(scope),
            variablesProvider: VariablesProvider = VariablesProvider.of(scope),
        ): LogicProgrammingScope = of(scope, termificator, variablesProvider, unificator, theoryFactory)
    }
}
