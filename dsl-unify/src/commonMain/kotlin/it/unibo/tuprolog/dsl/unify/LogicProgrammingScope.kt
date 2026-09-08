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

/**
 * Full term-construction *and* unification scope of `:dsl-unify`'s Prolog DSL: composes `:dsl-core`'s scope mixins
 * ([MinimalLogicProgrammingScope], [LogicProgrammingScopeWithSubstitutions], [LogicProgrammingScopeWithPrologStandardLibrary],
 * [LogicProgrammingScopeWithOperators], [LogicProgrammingScopeWithVariables] — none of which know anything about
 * unification) with [LogicProgrammingScopeWithUnification], so an [it.unibo.tuprolog.unify.Unificator]'s operations
 * (bare, or through the `mguWith`/`matches`/`unifyWith` sugar) are available alongside the rest of the DSL, all
 * self-referentially typed so chained calls keep resolving to this same, richer scope.
 *
 * [logicProgramming]/[lp] are the idiomatic way to obtain and use one, optionally choosing which [Unificator]
 * strategy governs it; [of] and [empty] exist for callers that need to plug in a custom [Termificator],
 * [VariablesProvider] or [Unificator] (or build a scope without immediately entering it).
 * [it.unibo.tuprolog.dsl.LogicProgrammingScope.withUnification] upgrades an existing `:dsl-core` scope into one of
 * these, reusing its underlying [Scope]/[Termificator]/[VariablesProvider].
 *
 * @see logicProgramming
 */
interface LogicProgrammingScope :
    MinimalLogicProgrammingScope<LogicProgrammingScope>,
    LogicProgrammingScopeWithSubstitutions<LogicProgrammingScope>,
    LogicProgrammingScopeWithPrologStandardLibrary<LogicProgrammingScope>,
    LogicProgrammingScopeWithOperators<LogicProgrammingScope>,
    LogicProgrammingScopeWithVariables<LogicProgrammingScope>,
    LogicProgrammingScopeWithUnification<LogicProgrammingScope> {
    companion object {
        internal val defaultUnificator = Unificator.default

        /** Shorthand for [of] with every parameter defaulted, i.e. a scope backed by a brand-new, empty [Scope]
         * and the [defaultUnificator] unification strategy. */
        @JsName("empty")
        fun empty(): LogicProgrammingScope = of()

        /**
         * Creates a new [LogicProgrammingScope] backed by [scope] (a fresh, empty one by default), using
         * [termificator] to convert plain values into [it.unibo.tuprolog.core.Term]s, [variablesProvider] to
         * create fresh variables, and [unificator] (the [defaultUnificator] strategy unless stated otherwise) to
         * unify them. If either [termificator] or [variablesProvider] is not already backed by [scope], it is
         * [Termificator.copy]/[VariablesProvider.copy]-ed onto it first, so all three end up sharing the very same
         * [Scope] instance (a requirement of the returned scope's implementation).
         */
        @JsName("of")
        fun of(
            scope: Scope = Scope.empty(),
            termificator: Termificator = Termificator.default(scope),
            variablesProvider: VariablesProvider = VariablesProvider.of(scope),
            unificator: Unificator = defaultUnificator,
        ): LogicProgrammingScope =
            LogicProgrammingScopeImpl(
                scope,
                if (termificator.scope === scope) termificator else termificator.copy(scope),
                if (variablesProvider.scope === scope) variablesProvider else variablesProvider.copy(scope),
                unificator,
            )

        /**
         * Overload of [of] taking [unificator] as its first parameter (rather than its last), so a non-default
         * [Unificator] can be supplied positionally without naming the argument — as [logicProgramming] and [lp] do.
         */
        @JsName("ofUnificator")
        fun of(
            unificator: Unificator,
            scope: Scope = Scope.empty(),
            termificator: Termificator = Termificator.default(scope),
            variablesProvider: VariablesProvider = VariablesProvider.of(scope),
        ): LogicProgrammingScope = of(scope, termificator, variablesProvider, unificator)
    }
}
