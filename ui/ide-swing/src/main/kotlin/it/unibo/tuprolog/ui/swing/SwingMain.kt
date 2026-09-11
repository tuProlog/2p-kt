@file:JvmName("Main")

package it.unibo.tuprolog.ui.swing

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.template.ClassicTheoryTemplates
import it.unibo.tuprolog.ui.gui.template.TheoryTemplate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val DEFAULT_TIMEOUT_MILLIS = 5_000L

private class SwingIdeCommand : CliktCommand(name = "ide-swing") {
    private val theories: List<String> by
        option("-T", "--theory", help = "Path of a theory file to open on startup").multiple()
    private val timeout: Long by
        option("-t", "--timeout", help = "Default resolution timeout in milliseconds")
            .long()
            .default(DEFAULT_TIMEOUT_MILLIS)

    override fun help(context: Context) = "Start the tuProlog Swing IDE"

    override fun run() =
        runBlocking {
            launchSwingIde(
                Solver.prolog,
                defaultTimeout = timeout.milliseconds,
                theoryFiles = theories.map(::File),
            )
        }
}

fun main(args: Array<String>) = SwingIdeCommand().main(args)

/**
 * This is the application's single top-level entry point: every parameter is an independent, named, defaulted
 * configuration knob (solver profile identity, extension wiring, persistence, timeouts, ...), not a symptom of
 * one function doing too much -- bundling them into a config object would just move the same parameter list one
 * level deeper without reducing complexity, and would break the flat, fully-named call style both real entry
 * points ([SwingIdeCommand]) and tests rely on.
 */
@Suppress("LongParameterList")
suspend fun launchSwingIde(
    factory: SolverFactory,
    profileId: SolverProfileId = SolverProfileId("prolog"),
    profileName: String = "Prolog",
    featureRenderers: SwingFeatureRendererRegistry = SwingFeatureRendererRegistry(),
    extensions: List<GuiExtension> = emptyList(),
    registerProfile: Boolean = true,
    capabilities: Set<String> = setOf(SolverCapabilities.CANCELLATION),
    solutionFeatures: (Solution) -> Map<FeatureId, Map<String, FeatureValue>> = { emptyMap() },
    templates: List<TheoryTemplate> = ClassicTheoryTemplates.ALL,
    persistence: WorkspacePersistence? = WorkspacePersistence("ide-swing"),
    theoryFiles: List<File> = emptyList(),
    defaultTimeout: Duration = 5.seconds,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    val profile = solverFactoryProfile(factory, profileId, profileName, capabilities, solutionFeatures)
    val scope = CoroutineScope(SupervisorJob() + dispatcher)
    val application =
        buildGuiApplication(scope) {
            if (registerProfile) solverProfile(profile, makeDefault = true) else defaultSolverProfile(profile.id)
            extensions.forEach(::extension)
            defaultTimeout(defaultTimeout)
        }
    SwingIdeApplication(application, scope, featureRenderers, templates, persistence).show(theoryFiles.isEmpty())
    for (file in theoryFiles) {
        runCatching { file.readText() }
            .onSuccess { text ->
                val origin = DocumentOrigin(JVM_PATH_PROVIDER, file.absolutePath, file.name)
                application.controller.dispatch(WorkspaceAction.OpenDocumentLoaded(origin, text))
            }.onFailure { System.err.println("Cannot read theory file: ${file.path} (${it.message})") }
    }
}
