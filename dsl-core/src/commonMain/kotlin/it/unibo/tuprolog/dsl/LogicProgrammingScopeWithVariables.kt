package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import kotlin.js.JsName

/**
 * Adds fresh-variable creation to the scope by mixing in [VariablesProvider] (delegating to [variablesProvider]),
 * so idiomatic single-letter variables (`A`, `B`, `C`, ... from [VariablesProvider]) and `by`-delegated ones
 * (`val x by variablesProvider`) are available directly inside a [logicProgramming] block, alongside every other
 * [BaseLogicProgrammingScope] mixin.
 */
interface LogicProgrammingScopeWithVariables<S : LogicProgrammingScopeWithVariables<S>> :
    BaseLogicProgrammingScope<S>,
    VariablesProvider {
    /** The [VariablesProvider] this scope's own [VariablesProvider] methods delegate to. */
    @JsName("variablesProvider")
    val variablesProvider: VariablesProvider

    /** Creates a copy of this scope, backed by [scope] (see [VariablesProvider.copy]). */
    override fun copy(scope: Scope): S
}
