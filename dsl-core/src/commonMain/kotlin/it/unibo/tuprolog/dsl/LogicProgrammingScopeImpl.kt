package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider

class LogicProgrammingScopeImpl private constructor(
    override val scope: Scope,
    override val termificator: Termificator,
    private val variablesProvider: VariablesProvider,
) : LogicProgrammingScope, VariablesProvider by variablesProvider {
    init {
        require(scope === variablesProvider.scope && scope === termificator.scope) {
            "The provided Scope should be the same object for both Termificator and VariablesProvider"
        }
    }

    private lateinit var termificatorFactory: (Scope) -> Termificator
    private lateinit var variablesProviderFactory: (Scope) -> VariablesProvider

    constructor(
        scope: Scope,
        termificatorFactory: (Scope) -> Termificator,
        variablesProviderFactory: (Scope) -> VariablesProvider,
    ) : this(scope, termificatorFactory(scope), variablesProviderFactory(scope)) {
        this.termificatorFactory = termificatorFactory
        this.variablesProviderFactory = variablesProviderFactory
    }

    override fun newScope(): LogicProgrammingScope =
        Scope.empty().let {
            LogicProgrammingScopeImpl(it, termificatorFactory(it), variablesProviderFactory(it))
        }
}
