package it.unibo.tuprolog.ui.swing.plp

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.plp.PlpGuiExtension
import it.unibo.tuprolog.ui.gui.plp.PlpTheoryTemplates
import it.unibo.tuprolog.ui.gui.plp.plpFeatureState
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.swing.WorkspacePersistence
import it.unibo.tuprolog.ui.swing.launchSwingIde
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

private class PlpSwingIdeCommand : CliktCommand(name = "ide-plp-swing") {
    private val theories: List<String> by
        option("-T", "--theory", help = "Path of a theory file to open on startup").multiple()
    private val timeout: Long by
        option("-t", "--timeout", help = "Default resolution timeout in milliseconds").long().default(5_000)

    override fun help(context: Context) = "Start the tuProlog PLP (ProbLog) Swing IDE"

    override fun run() =
        runBlocking {
            val capabilities =
                setOf(
                    SolverCapabilities.CANCELLATION,
                    SolverCapabilities.PROBABILISTIC_SOLUTIONS,
                    SolverCapabilities.BDD_PRESENTATION,
                )
            val profile =
                solverFactoryProfile(
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
                theoryFiles = theories.map(::File),
                defaultTimeout = timeout.milliseconds,
            )
        }
}

fun main(args: Array<String>) = PlpSwingIdeCommand().main(args)
