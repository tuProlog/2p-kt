package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile

/**
 * Like [solverFactoryProfile], but every resolution runs with probabilistic solving enabled (see
 * [PlpSolverFactorySession]); [SolverCapabilities.PROBABILISTIC_SOLUTIONS] is added automatically so callers
 * can't end up with a profile that behaviorally enables it without advertising it.
 */
fun plpSolverFactoryProfile(
    factory: SolverFactory,
    id: SolverProfileId,
    displayName: String,
    capabilities: Set<String> = setOf(SolverCapabilities.CANCELLATION),
    solutionFeatures: (Solution) -> Map<FeatureId, Map<String, FeatureValue>> = { emptyMap() },
): SolverProfile =
    solverFactoryProfile(
        factory,
        id,
        displayName,
        capabilities + SolverCapabilities.PROBABILISTIC_SOLUTIONS,
        solutionFeatures,
        newSession = ::PlpSolverFactorySession,
    )
