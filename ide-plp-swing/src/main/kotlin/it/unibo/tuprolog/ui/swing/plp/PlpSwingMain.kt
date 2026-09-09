package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.bdd.toDotString
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.binaryDecisionDiagram
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.plp.BddPresentation
import it.unibo.tuprolog.ui.gui.plp.PlpGuiExtension
import it.unibo.tuprolog.ui.gui.plp.PlpSolutionDetails
import it.unibo.tuprolog.ui.gui.plp.PlpTheoryTemplates
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.swing.WorkspacePersistence
import it.unibo.tuprolog.ui.swing.launchSwingIde
import it.unibo.tuprolog.ui.swing.swingSolverProfile
import kotlinx.coroutines.runBlocking

fun main() =
    runBlocking {
        val capabilities =
            setOf(
                SolverCapabilities.CANCELLATION,
                SolverCapabilities.PROBABILISTIC_SOLUTIONS,
                SolverCapabilities.BDD_PRESENTATION,
            )
        val profile =
            swingSolverProfile(
                Solver.problog,
                SolverProfileId("problog"),
                "ProbLog",
                capabilities,
                Solution::plpFeatureState,
            )
        launchSwingIde(
            factory = Solver.problog,
            profileId = profile.id,
            profileName = profile.displayName,
            featureRenderers = plpSwingFeatureRenderers(),
            extensions = listOf(PlpGuiExtension(profile)),
            registerProfile = false,
            capabilities = capabilities,
            templates = PlpTheoryTemplates.ALL,
            persistence = WorkspacePersistence("ide-plp-swing"),
        )
    }

internal fun Solution.plpFeatureState() =
    PlpSolutionDetails(
        probability = (this as? Solution.Yes)?.probability,
        bdd = binaryDecisionDiagram?.let { BddPresentation(it.toDotString(), toString()) },
    ).toFeatureStateReplacements()
