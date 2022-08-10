package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.dsl.LogicProgrammingScope
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithUnificationImpl(unificator: Unificator) :
    LogicProgrammingScopeWithUnification, LogicProgrammingScope by LogicProgrammingScope.empty(), Unificator by unificator {

    constructor() : this(Unificator.default)
}
