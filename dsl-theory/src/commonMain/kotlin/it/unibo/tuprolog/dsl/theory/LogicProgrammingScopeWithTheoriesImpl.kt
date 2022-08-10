package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.dsl.LogicProgrammingScope
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithTheoriesImpl(unificator: Unificator) :
    LogicProgrammingScopeWithTheories, LogicProgrammingScope by LogicProgrammingScope.empty(), Unificator by unificator {

    constructor() : this(Unificator.default)
}
