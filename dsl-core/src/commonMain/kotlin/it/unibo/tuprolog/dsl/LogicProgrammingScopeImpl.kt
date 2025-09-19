package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider

internal class LogicProgrammingScopeImpl(
    override val scope: Scope,
    override val termificator: Termificator,
    override val variablesProvider: VariablesProvider,
) : LogicProgrammingScope,
    VariablesProvider by variablesProvider {
    init {
        require(scope === variablesProvider.scope && scope === termificator.scope) {
            "The provided Scope should be the same object for both Termificator and VariablesProvider"
        }
    }

    override fun copy(scope: Scope): LogicProgrammingScope =
        LogicProgrammingScopeImpl(scope, termificator.copy(scope), variablesProvider.copy(scope))

    override fun newScope(): LogicProgrammingScope = copy(Scope.empty())
}
