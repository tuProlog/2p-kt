package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider

class LogicProgrammingScopeImpl private constructor(
    override val scope: Scope,
    override val termificator: Termificator,
    private val variablesProvider: VariablesProvider,
) : LogicProgrammingScope,
    Termificator by termificator,
    VariablesProvider by variablesProvider {
    init {
        require(scope === variablesProvider.scope && scope === termificator.scope) {
            "The provided Scope should be the same object for both Termificator and VariablesProvider"
        }
    }

    constructor(
        scope: Scope,
        termificatorFactory: (Scope) -> Termificator,
        variablesProviderFactory: (Scope) -> VariablesProvider,
    ) : this(scope, termificatorFactory(scope), variablesProviderFactory(scope))

    override fun newScope(): LogicProgrammingScope = LogicProgrammingScopeImpl(scope, termificator, variablesProvider)
}
