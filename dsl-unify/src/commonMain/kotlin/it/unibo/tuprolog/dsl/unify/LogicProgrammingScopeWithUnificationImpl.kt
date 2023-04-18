package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.dsl.LogicProgrammingScope
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithUnificationImpl(
    override val unificator: Unificator,
    scope: Scope
) : LogicProgrammingScopeWithUnification,
    LogicProgrammingScope by LogicProgrammingScope.of(scope),
    Unificator by unificator
