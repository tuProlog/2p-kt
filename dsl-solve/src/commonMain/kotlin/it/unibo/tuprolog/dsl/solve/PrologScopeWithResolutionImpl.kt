package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.dsl.theory.PrologScopeWithTheories
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory

class PrologScopeWithResolutionImpl
    constructor(
        override val solverFactory: SolverFactory,
        override val defaultSolver: MutableSolver = solverFactory.mutableSolverOf()
    ) : PrologScopeWithResolution, PrologScopeWithTheories by PrologScopeWithTheories.empty(), MutableSolver by defaultSolver