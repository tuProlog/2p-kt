package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.dsl.theory.LogicProgrammingScopeWithTheories
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory

class LogicProgrammingScopeWithResolutionImpl
constructor(
    override val solverFactory: SolverFactory,
    override val defaultSolver: MutableSolver = solverFactory.mutableSolverWithDefaultBuiltins()
) : LogicProgrammingScopeWithResolution, LogicProgrammingScopeWithTheories by LogicProgrammingScopeWithTheories.empty(), MutableSolver by defaultSolver
