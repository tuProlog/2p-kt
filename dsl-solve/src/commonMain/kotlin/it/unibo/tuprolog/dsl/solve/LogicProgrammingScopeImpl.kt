package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.dsl.solve.LogicProgrammingScope.Companion.changeUnificatorIfNecessary
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.theory.TheoryFactory
import it.unibo.tuprolog.unify.Unificator

class LogicProgrammingScopeImpl private constructor(
    override val scope: Scope,
    override val termificator: Termificator,
    override val variablesProvider: VariablesProvider,
    override val unificator: Unificator,
    override val theoryFactory: TheoryFactory,
    override val solverFactory: SolverFactory,
    override val defaultSolver: MutableSolver,
) : LogicProgrammingScope,
    VariablesProvider by variablesProvider,
    Unificator by unificator,
    TheoryFactory by theoryFactory,
    MutableSolver by defaultSolver {
    init {
        require(scope === variablesProvider.scope && scope === termificator.scope) {
            "The provided Scope should be the same object for both Termificator and VariablesProvider"
        }
        require(
            unificator == theoryFactory.unificator &&
                unificator == solverFactory.defaultUnificator &&
                unificator == defaultSolver.unificator,
        ) {
            "The provided Unificator should be the same object for both Solver, SolverFactory, and TheoryFactory"
        }
    }

    constructor(
        scope: Scope,
        termificator: Termificator,
        variablesProvider: VariablesProvider,
        unificator: Unificator,
        theoryFactory: TheoryFactory,
        solverFactory: SolverFactory,
    ) : this(
        scope,
        termificator,
        variablesProvider,
        unificator,
        theoryFactory,
        solverFactory,
        solverFactory.mutableSolverOf(unificator),
    )

    override fun newScope(): LogicProgrammingScope = copy(Scope.empty())

    override fun copy(scope: Scope): LogicProgrammingScope =
        LogicProgrammingScopeImpl(
            scope,
            termificator.copy(scope),
            variablesProvider.copy(scope),
            unificator,
            theoryFactory,
            solverFactory,
        )

    override fun copy(unificator: Unificator): LogicProgrammingScope =
        LogicProgrammingScopeImpl(
            scope,
            termificator,
            variablesProvider,
            unificator,
            theoryFactory.copy(unificator),
            solverFactory.changeUnificatorIfNecessary(unificator),
        )
}
