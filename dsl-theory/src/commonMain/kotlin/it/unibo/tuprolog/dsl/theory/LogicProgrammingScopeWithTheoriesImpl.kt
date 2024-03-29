package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScope
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithTheoriesImpl(
    unificator: Unificator,
    scope: Scope,
) : LogicProgrammingScopeWithTheories,
    LogicProgrammingScope by LogicProgrammingScope.of(unificator, scope)
