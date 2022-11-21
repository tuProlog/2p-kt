package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.dsl.theory.LogicProgrammingScopeWithTheories
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithResolutionImpl(
    override val solverFactory: SolverFactory,
    override val unificator: Unificator,
    override val defaultSolver: MutableSolver = solverFactory.mutableSolverWithDefaultBuiltins()
) : LogicProgrammingScopeWithResolution,
    LogicProgrammingScopeWithTheories by LogicProgrammingScopeWithTheories.of(unificator),
    MutableSolver by defaultSolver
