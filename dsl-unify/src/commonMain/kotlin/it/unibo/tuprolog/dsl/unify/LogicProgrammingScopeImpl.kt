package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeImpl internal constructor(
    override val scope: Scope,
    override val termificator: Termificator,
    override val variablesProvider: VariablesProvider,
    override val unificator: Unificator,
) : LogicProgrammingScope, VariablesProvider by variablesProvider, Unificator by unificator {
    init {
        require(scope === variablesProvider.scope && scope === termificator.scope) {
            "The provided Scope should be the same object for both Termificator and VariablesProvider"
        }
    }

    private lateinit var termificatorFactory: (Scope) -> Termificator
    private lateinit var variablesProviderFactory: (Scope) -> VariablesProvider
    private lateinit var unificatorFactory: (Scope) -> Unificator

    constructor(
        scope: Scope,
        termificatorFactory: (Scope) -> Termificator,
        variablesProviderFactory: (Scope) -> VariablesProvider,
        unificatorFactory: (Scope) -> Unificator,
    ) : this(scope, termificatorFactory(scope), variablesProviderFactory(scope), unificatorFactory(scope)) {
        this.termificatorFactory = termificatorFactory
        this.variablesProviderFactory = variablesProviderFactory
        this.unificatorFactory = unificatorFactory
    }

    override fun newScope(): LogicProgrammingScope =
        Scope.empty().let {
            LogicProgrammingScopeImpl(
                it,
                termificatorFactory(it),
                variablesProviderFactory(it),
                unificatorFactory(it),
            )
        }
}
