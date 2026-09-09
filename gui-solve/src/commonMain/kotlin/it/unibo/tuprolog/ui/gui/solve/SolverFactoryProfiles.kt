package it.unibo.tuprolog.ui.gui.solve

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile

private val INSPECTION_CAPABILITIES =
    setOf(
        SolverCapabilities.STATIC_KB_INSPECTION,
        SolverCapabilities.DYNAMIC_KB_INSPECTION,
        SolverCapabilities.OPERATORS_INSPECTION,
        SolverCapabilities.FLAGS_INSPECTION,
        SolverCapabilities.LIBRARIES_INSPECTION,
        SolverCapabilities.INTERACTIVE_INPUT,
    )

/** Builds a [SolverProfile] backed by any [SolverFactory] (classic, ProbLog, ...); no toolkit dependency. */
fun solverFactoryProfile(
    factory: SolverFactory,
    id: SolverProfileId,
    displayName: String,
    capabilities: Set<String> = setOf(SolverCapabilities.CANCELLATION),
    solutionFeatures: (Solution) -> Map<FeatureId, Map<String, FeatureValue>> = { emptyMap() },
): SolverProfile =
    SolverProfile(
        id = id,
        displayName = displayName,
        capabilities = SolverCapabilities(capabilities + INSPECTION_CAPABILITIES),
        factory = { request ->
            SolverFactorySession(factory, request, capabilities + INSPECTION_CAPABILITIES, solutionFeatures)
        },
    )
