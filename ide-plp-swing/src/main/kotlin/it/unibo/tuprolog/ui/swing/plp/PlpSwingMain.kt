package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.plp.PlpGuiExtension
import it.unibo.tuprolog.ui.swing.launchSwingIde
import it.unibo.tuprolog.ui.swing.swingSolverProfile
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val capabilities = setOf(
        SolverCapabilities.CANCELLATION,
        SolverCapabilities.PROBABILISTIC_SOLUTIONS,
        SolverCapabilities.BDD_PRESENTATION,
    )
    val profile = swingSolverProfile(Solver.problog, SolverProfileId("problog"), "ProbLog", capabilities)
    launchSwingIde(
        factory = Solver.problog,
        profileId = profile.id,
        profileName = profile.displayName,
        featureRenderers = plpSwingFeatureRenderers(),
        extensions = listOf(PlpGuiExtension(profile)),
        registerProfile = false,
        capabilities = capabilities,
    )
}
