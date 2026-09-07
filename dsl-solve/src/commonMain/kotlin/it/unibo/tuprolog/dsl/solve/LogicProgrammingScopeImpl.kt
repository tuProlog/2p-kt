package it.unibo.tuprolog.dsl.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.VariablesProvider
import it.unibo.tuprolog.dsl.Termificator
import it.unibo.tuprolog.dsl.solve.LogicProgrammingScope.Companion.changeUnificatorIfNecessary
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.theory.TheoryFactory
import it.unibo.tuprolog.unify.Unificator

/**
 * Default [LogicProgrammingScope] implementation: forwards [VariablesProvider], [Unificator], [TheoryFactory] and
 * [MutableSolver] operations to its respective collaborator ([variablesProvider], [unificator], [theoryFactory],
 * [defaultSolver]), and requires ([init]) that they all share the same [Scope]/[Unificator] consistently. Built by
 * [LogicProgrammingScope.of] (and transitively by [logicProgramming]/[lp]/[prolog]) rather than instantiated
 * directly by client code.
 *
 * @throws IllegalArgumentException if [scope] is not the same object backing both [termificator] and
 * [variablesProvider], or if [unificator] is not the same object used by [theoryFactory], [solverFactory] and
 * [defaultSolver].
 */
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

    /** Builds [defaultSolver] from [solverFactory]/[unificator] via [SolverFactory.mutableSolverOf]. */
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

    /** Returns a fresh scope sharing this one's [unificator], [theoryFactory] and [solverFactory] but a brand-new, empty [Scope]. */
    override fun newScope(): LogicProgrammingScope = copy(Scope.empty())

    /** Returns a copy of this scope backed by [scope] instead, keeping [unificator], [theoryFactory] and [solverFactory] as-is. */
    override fun copy(scope: Scope): LogicProgrammingScope =
        LogicProgrammingScopeImpl(
            scope,
            termificator.copy(scope),
            variablesProvider.copy(scope),
            unificator,
            theoryFactory,
            solverFactory,
        )

    /**
     * Returns a copy of this scope using [unificator] instead of the current one, propagating it to [theoryFactory]
     * and [solverFactory] (rebuilding [solverFactory] only if its own default [Unificator] differs from
     * [unificator]) — note this rebuilds [defaultSolver] too, so any static/dynamic KB or configuration already
     * loaded into it is lost.
     */
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
