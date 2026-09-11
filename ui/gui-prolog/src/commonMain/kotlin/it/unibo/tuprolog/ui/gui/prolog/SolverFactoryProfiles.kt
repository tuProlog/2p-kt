package it.unibo.tuprolog.ui.gui.prolog

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
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

/**
 * Builds a [SolverProfile] backed by any [SolverFactory] (classic, ProbLog, ...); no toolkit dependency.
 *
 * [runtimeLibraries] defaults to [OOPLib] and [IOLib], matching every existing caller (e.g. ide-swing). Pass a
 * narrower list on platforms where a default library doesn't work: [OOPLib] is reflection-based and throws
 * [NotImplementedError] on Kotlin/JS the moment a runtime tries to use it, which previously failed every single
 * resolution in ide-web regardless of whether the query needed OOP features at all.
 */
fun solverFactoryProfile(
    factory: SolverFactory,
    id: SolverProfileId,
    displayName: String,
    capabilities: Set<String> = setOf(SolverCapabilities.CANCELLATION),
    solutionFeatures: (Solution) -> Map<FeatureId, Map<String, FeatureValue>> = { emptyMap() },
    runtimeLibraries: List<Library> = listOf(OOPLib, IOLib),
): SolverProfile =
    SolverProfile(
        id = id,
        displayName = displayName,
        capabilities = SolverCapabilities(capabilities + INSPECTION_CAPABILITIES),
        factory = { request ->
            SolverFactorySession(
                factory,
                request,
                capabilities + INSPECTION_CAPABILITIES,
                solutionFeatures,
                runtimeLibraries,
            )
        },
    )
