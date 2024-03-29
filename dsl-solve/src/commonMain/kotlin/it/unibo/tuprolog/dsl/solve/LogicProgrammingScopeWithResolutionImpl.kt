package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeWithResolutionImpl(
    override val solverFactory: SolverFactory,
    override val unificator: Unificator,
    scope: Scope,
    override val defaultSolver: MutableSolver = solverFactory.mutableSolverWithDefaultBuiltins(),
) : LogicProgrammingScopeWithResolution,
    LogicProgrammingScope by LogicProgrammingScope.of(unificator, scope),
    MutableSolver by defaultSolver
