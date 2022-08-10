package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnification
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithTheoriesImpl(unificator: Unificator) : LogicProgrammingScopeWithTheories,
    LogicProgrammingScopeWithUnification by LogicProgrammingScopeWithUnification.of(unificator)
