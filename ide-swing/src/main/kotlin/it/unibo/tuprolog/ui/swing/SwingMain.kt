package it.unibo.tuprolog.ui.swing

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.core.parsing.parseAsTerm
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.TrackVariables.ON
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionCursor
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSession
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import it.unibo.tuprolog.ui.gui.template.ClassicTheoryTemplates
import it.unibo.tuprolog.ui.gui.template.TheoryTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private class SwingIdeCommand : CliktCommand(name = "ide-swing") {
    private val theories: List<String> by
        option("-T", "--theory", help = "Path of a theory file to open on startup").multiple()
    private val timeout: Long by
        option("-t", "--timeout", help = "Default resolution timeout in milliseconds").long().default(5_000)

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
) {
    val profile = swingSolverProfile(factory, profileId, profileName, capabilities, solutionFeatures)
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
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

fun swingSolverProfile(
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
            SwingSolverSession(factory, request, capabilities + INSPECTION_CAPABILITIES, solutionFeatures)
        },
    )

private val INSPECTION_CAPABILITIES =
    setOf(
        SolverCapabilities.STATIC_KB_INSPECTION,
        SolverCapabilities.DYNAMIC_KB_INSPECTION,
        SolverCapabilities.OPERATORS_INSPECTION,
        SolverCapabilities.FLAGS_INSPECTION,
        SolverCapabilities.LIBRARIES_INSPECTION,
        SolverCapabilities.INTERACTIVE_INPUT,
    )

private class SwingSolverSession(
    private val factory: SolverFactory,
    private val creationRequest: SolverSessionCreationRequest,
    capabilities: Set<String>,
    private val solutionFeatures: (Solution) -> Map<FeatureId, Map<String, FeatureValue>>,
) : SolverSession {
    private val pendingSignals = mutableListOf<SolverSignal>()
    private var solver = newSolver()

    override val id = SolverSessionId("swing-${creationRequest.pageId.value}-${creationRequest.documentRevision}")
    override val capabilities = SolverCapabilities(capabilities)
    override val snapshot: SolverInspectionSnapshot
        get() = solver.inspectionSnapshot()

    override suspend fun openResolution(request: ResolutionRequest): ResolutionCursor {
        val query =
            request.query
                .trim()
                .removeSuffix(".")
                .parseAsStruct(solver.operators)
        val options =
            if (request.timeout.inWholeMilliseconds == 0L) {
                SolveOptions.allLazily()
            } else {
                SolveOptions.allLazilyWithTimeout(request.timeout.inWholeMilliseconds)
            }
        val solutions = solver.solve(query, options).iterator()
        return object : ResolutionCursor {
            override suspend fun next(): ResolutionStep {
                yield()
                return if (!solutions.hasNext()) {
                    ResolutionStep.End(signals = drainSignals())
                } else {
                    val solution = solutions.next()
                    solution.toStep(request.query, drainSignals(), solutionFeatures(solution))
                }
            }

            override suspend fun cancel() = Unit
        }
    }

    override suspend fun reset(): SolverInspectionSnapshot {
        solver = newSolver()
        return snapshot
    }

    override suspend fun close() = Unit

    private fun newSolver(): MutableSolver {
        var builder =
            factory
                .newBuilder()
                .runtime(Runtime.of(OOPLib, IOLib))
                .flag(TrackVariables) { ON }
                .standardInput(InputChannel.of(creationRequest.stdin))
                .standardOutput(OutputChannel.of { signal(SolverSignal.Stdout(it)) })
                .standardError(OutputChannel.of { signal(SolverSignal.Stderr(it)) })
                .warnings(
                    OutputChannel.of {
                        signal(
                            SolverSignal.Warning(
                                WarningPresentation(
                                    it.message.orEmpty(),
                                    it.logicStackTrace.map(Any::toString),
                                ),
                            ),
                        )
                    },
                )
        for ((name, value) in creationRequest.options) {
            builder = runCatching { builder.flag(name to value.parseAsTerm()) }.getOrDefault(builder)
        }
        return builder
            .buildMutable()
            .also { it.loadStaticKb(creationRequest.sourceText.parseAsTheory(it.operators)) }
    }

    private fun signal(signal: SolverSignal) {
        synchronized(pendingSignals) { pendingSignals += signal }
    }

    private fun drainSignals(): List<SolverSignal> =
        synchronized(pendingSignals) {
            (pendingSignals.toList() + SolverSignal.Inspection(snapshot)).also { pendingSignals.clear() }
        }
}

private fun Solution.toStep(
    queryText: String,
    signals: List<SolverSignal>,
    features: Map<FeatureId, Map<String, FeatureValue>>,
): ResolutionStep =
    when (this) {
        is Solution.Yes ->
            ResolutionStep.Yield(
                SolutionPresentation.Yes(
                    query = queryText,
                    bindings =
                        query.variables
                            .filterNot(Var::isAnonymous)
                            .mapNotNull { variable ->
                                valueOf(variable)?.let { BindingPresentation(variable.name, it.toString()) }
                            }.toList(),
                    solvedQuery = solvedQuery.toString(),
                ),
                hasMorePotentially = true,
                signals = signals,
                featureStateReplacements = features,
            )
        is Solution.No ->
            ResolutionStep.Yield(
                SolutionPresentation.No(queryText),
                hasMorePotentially = false,
                signals = signals,
                featureStateReplacements = features,
            )
        is Solution.Halt ->
            ResolutionStep.Yield(
                SolutionPresentation.Halt(
                    queryText,
                    exception.message ?: "Resolution halted",
                    exception.logicStackTrace.map {
                        it.toString()
                    },
                ),
                hasMorePotentially = false,
                signals = signals,
                featureStateReplacements = features,
            )
    }

private fun Solver.inspectionSnapshot(): SolverInspectionSnapshot =
    SolverInspectionSnapshot(
        operators = operators.map { OperatorPresentation(it.functor, it.priority, it.specifier.name) },
        flags = flags.entries.map { FlagPresentation(it.key, it.value.toString()) }.sortedBy { it.name },
        libraries =
            libraries.libraries
                .map { library ->
                    LibraryPresentation(
                        alias = library.alias,
                        predicates =
                            (library.primitives.keys.asSequence() + library.rulesSignatures)
                                .map(Signature::format)
                                .distinct()
                                .sorted()
                                .toList(),
                        operators =
                            library.operators.map {
                                OperatorPresentation(
                                    it.functor,
                                    it.priority,
                                    it.specifier.name,
                                )
                            },
                        functions =
                            library.functions.keys
                                .map(Signature::format)
                                .sorted(),
                    )
                }.sortedBy { it.alias },
        staticKnowledgeBase = staticKb.joinToString("\n") { "$it." },
        dynamicKnowledgeBase = dynamicKb.joinToString("\n") { "$it." },
    )

private fun Signature.format(): String = "$name/$arity${if (vararg) "+" else ""}"
