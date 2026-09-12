@file:JvmName("Main")

package it.unibo.tuprolog.ui.swing.plp

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
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
import it.unibo.tuprolog.ui.swing.installedLookAndFeels
import it.unibo.tuprolog.ui.swing.launchSwingIde
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

private const val DEFAULT_TIMEOUT_MILLIS = 5_000L

private class PlpSwingIdeCommand : CliktCommand(name = "ide-plp-swing") {
    private val theories: List<String> by
        option("-T", "--theory", help = "Path of a theory file to open on startup").multiple()
    private val timeout: Long by
        option("-t", "--timeout", help = "Default resolution timeout in milliseconds")
            .long()
            .default(DEFAULT_TIMEOUT_MILLIS)
    private val lookAndFeel: String? by
        option("-l", "--look-and-feel", help = "Name of an installed Swing look-and-feel to start with")
    private val listLookAndFeels: Boolean by
        option("--list-look-and-feels", help = "List the look-and-feel names installed on this JVM and exit")
            .flag()

    override fun help(context: Context) = "Start the tuProlog PLP (ProbLog) Swing IDE"

    override fun run() =
        runBlocking {
            if (listLookAndFeels) {
                installedLookAndFeels().forEach { println(it.name) }
                return@runBlocking
            }
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
                lookAndFeel = lookAndFeel,
            )
        }
}

fun main(args: Array<String>) = PlpSwingIdeCommand().main(args)
