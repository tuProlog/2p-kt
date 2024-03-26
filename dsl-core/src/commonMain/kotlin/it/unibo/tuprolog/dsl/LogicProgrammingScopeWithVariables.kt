package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.VariablesProvider

interface LogicProgrammingScopeWithVariables<S : LogicProgrammingScopeWithVariables<S>> :
    BaseLogicProgrammingScope<S>, VariablesProvider
