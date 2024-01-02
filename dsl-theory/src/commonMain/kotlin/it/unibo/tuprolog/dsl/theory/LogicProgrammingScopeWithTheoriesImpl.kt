package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnification
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithTheoriesImpl(
    unificator: Unificator,
    scope: Scope,
) : LogicProgrammingScopeWithTheories,
    LogicProgrammingScopeWithUnification by LogicProgrammingScopeWithUnification.of(unificator, scope)
