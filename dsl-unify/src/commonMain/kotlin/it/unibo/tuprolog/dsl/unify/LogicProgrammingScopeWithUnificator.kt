package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.dsl.BaseLogicProgrammingScope
import it.unibo.tuprolog.unify.UnificationAware
import it.unibo.tuprolog.unify.Unificator

interface LogicProgrammingScopeWithUnificator<S : LogicProgrammingScopeWithUnificator<S>> :
    BaseLogicProgrammingScope<S>,
    UnificationAware,
    Unificator {
    fun copy(unificator: Unificator): S
}
