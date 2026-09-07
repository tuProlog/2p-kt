package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import kotlin.js.JsName

/**
 * Full term-construction scope of `:dsl-core`'s Prolog DSL: composes every mixin this module defines —
 * [MinimalLogicProgrammingScope] (structs, lists, clauses), [LogicProgrammingScopeWithSubstitutions],
 * [LogicProgrammingScopeWithPrologStandardLibrary], [LogicProgrammingScopeWithOperators] and
 * [LogicProgrammingScopeWithVariables] — into a single receiver type, self-referential (each mixin's own
 * [BaseLogicProgrammingScope] type parameter is bound back to [LogicProgrammingScope] itself) so that
 * [BaseLogicProgrammingScope.newScope]/`scope`/`rule`/`fact`/... all keep resolving to this same, richest type.
 *
 * [logicProgramming]/[lp] are the idiomatic way to obtain and use one; [of] and [empty] exist for callers that
 * need to plug in a custom [Termificator] or [VariablesProvider] (or build a scope without immediately entering
 * it). Further modules in the DSL stack (`:dsl-unify`, `:dsl-theory`, `:dsl-solve`) each define their own
 * `LogicProgrammingScope` extending this one with more mixins (unification, theories, resolution).
 *
 * @see logicProgramming
 */
interface LogicProgrammingScope :
    MinimalLogicProgrammingScope<LogicProgrammingScope>,
    LogicProgrammingScopeWithSubstitutions<LogicProgrammingScope>,
    LogicProgrammingScopeWithPrologStandardLibrary<LogicProgrammingScope>,
    LogicProgrammingScopeWithOperators<LogicProgrammingScope>,
    LogicProgrammingScopeWithVariables<LogicProgrammingScope> {
    companion object {
        /** Shorthand for [of] with every parameter defaulted, i.e. a scope backed by a brand-new, empty [Scope]. */
        @JsName("empty")
        fun empty(): LogicProgrammingScope = of()

        /**
         * Creates a new [LogicProgrammingScope] backed by [scope] (a fresh, empty one by default), using
         * [termificator] to convert plain values into [it.unibo.tuprolog.core.Term]s and [variablesProvider] to
         * create fresh variables. If either [termificator] or [variablesProvider] is not already backed by
         * [scope], it is [Termificator.copy]/[VariablesProvider.copy]-ed onto it first, so all three end up
         * sharing the very same [Scope] instance (a requirement of the returned scope's implementation).
         */
        @JsName("of")
        fun of(
            scope: Scope = Scope.empty(),
            termificator: Termificator = Termificator.default(scope),
            variablesProvider: VariablesProvider = VariablesProvider.of(scope),
        ): LogicProgrammingScope =
            LogicProgrammingScopeImpl(
                scope,
                if (termificator.scope === scope) termificator else termificator.copy(scope),
                if (variablesProvider.scope === scope) variablesProvider else variablesProvider.copy(scope),
            )
    }
}
