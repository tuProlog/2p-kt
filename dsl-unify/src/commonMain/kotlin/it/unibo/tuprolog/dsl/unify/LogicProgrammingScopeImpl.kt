package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.unify.Unificator

internal class LogicProgrammingScopeImpl(
    override val scope: Scope,
    override val termificator: Termificator,
    override val variablesProvider: VariablesProvider,
    override val unificator: Unificator,
) : LogicProgrammingScope,
    VariablesProvider by variablesProvider,
    Unificator by unificator {
    init {
        require(scope === variablesProvider.scope && scope === termificator.scope) {
            "The provided Scope should be the same object for both Termificator and VariablesProvider"
        }
    }

    override fun copy(scope: Scope): LogicProgrammingScope =
        LogicProgrammingScopeImpl(
            scope,
            termificator.copy(scope),
            variablesProvider.copy(scope),
            unificator,
        )

    override fun copy(unificator: Unificator): LogicProgrammingScope =
        LogicProgrammingScopeImpl(
            scope,
            termificator,
            variablesProvider,
            unificator,
        )

    override fun newScope(): LogicProgrammingScope = copy(Scope.empty())
}
