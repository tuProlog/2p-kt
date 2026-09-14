package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.setProbabilistic
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.prolog.SolverFactorySession
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest

/** [SolverFactorySession] specialization that enables probabilistic solving for every resolution it opens. */
internal class PlpSolverFactorySession(
    factory: SolverFactory,
    creationRequest: SolverSessionCreationRequest,
    capabilities: Set<String>,
    solutionFeatures: (Solution) -> Map<FeatureId, Map<String, FeatureValue>>,
    runtimeLibraries: List<Library>,
) : SolverFactorySession(factory, creationRequest, capabilities, solutionFeatures, runtimeLibraries) {
    override fun configureSolveOptions(options: SolveOptions): SolveOptions = options.setProbabilistic(true)
}
