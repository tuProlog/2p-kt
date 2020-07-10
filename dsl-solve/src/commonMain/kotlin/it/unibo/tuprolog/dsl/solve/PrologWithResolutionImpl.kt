package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.dsl.theory.PrologWithTheories
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory

class PrologWithResolutionImpl
    constructor(
        override val solverFactory: SolverFactory,
        override val defaultSolver: MutableSolver = solverFactory.mutableSolverOf()
    ) : PrologWithResolution, PrologWithTheories by PrologWithTheories.empty(), MutableSolver by defaultSolver