package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.solver.ResolutionCursor
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSession
import it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() = runBlocking {
    launchSwingIde(Solver.prolog)
}

suspend fun launchSwingIde(
    factory: SolverFactory,
    profileId: SolverProfileId = SolverProfileId("prolog"),
    profileName: String = "Prolog",
    featureRenderers: SwingFeatureRendererRegistry = SwingFeatureRendererRegistry(),
    extensions: List<GuiExtension> = emptyList(),
    registerProfile: Boolean = true,
    capabilities: Set<String> = setOf(SolverCapabilities.CANCELLATION),
) {
    val profile = swingSolverProfile(factory, profileId, profileName, capabilities)
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val application = buildGuiApplication(scope) {
        if (registerProfile) solverProfile(profile, makeDefault = true) else defaultSolverProfile(profile.id)
        extensions.forEach(::extension)
    }
    SwingIdeApplication(application, scope, featureRenderers).show()
}

fun swingSolverProfile(
    factory: SolverFactory,
    id: SolverProfileId,
    displayName: String,
    capabilities: Set<String> = setOf(SolverCapabilities.CANCELLATION),
): SolverProfile = SolverProfile(
    id = id,
    displayName = displayName,
    capabilities = SolverCapabilities(capabilities),
    factory = { request -> SwingSolverSession(factory, request) },
)

private class SwingSolverSession(
    factory: SolverFactory,
    request: SolverSessionCreationRequest,
) : SolverSession {
    private val solver = factory.mutableSolverWithDefaultBuiltins(staticKb = request.sourceText.parseAsTheory())

    override val id = SolverSessionId("swing-${request.pageId.value}-${request.documentRevision}")
    override val capabilities = SolverCapabilities(setOf(SolverCapabilities.CANCELLATION))
    override val snapshot = SolverInspectionSnapshot(staticKnowledgeBase = request.sourceText)

    override suspend fun openResolution(request: ResolutionRequest): ResolutionCursor {
        val query = request.query.trim().removeSuffix(".").parseAsStruct()
        val solutions = solver.solve(query, SolveOptions.allLazilyWithTimeout(request.timeout.inWholeMilliseconds)).iterator()
        return object : ResolutionCursor {
            override suspend fun next(): ResolutionStep {
                yield()
                return if (!solutions.hasNext()) ResolutionStep.End() else solutions.next().toStep(request.query)
            }

            override suspend fun cancel() = Unit
        }
    }

    override suspend fun reset(): SolverInspectionSnapshot = snapshot

    override suspend fun close() = Unit
}

private fun Solution.toStep(queryText: String): ResolutionStep = when (this) {
    is Solution.Yes -> ResolutionStep.Yield(
        SolutionPresentation.Yes(
            query = queryText,
            bindings = query.variables.filterNot(Var::isAnonymous).mapNotNull { variable ->
                valueOf(variable)?.let { BindingPresentation(variable.name, it.toString()) }
            }.toList(),
            solvedQuery = solvedQuery.toString(),
        ),
        hasMorePotentially = true,
    )
    is Solution.No -> ResolutionStep.Yield(SolutionPresentation.No(queryText), hasMorePotentially = false)
    is Solution.Halt -> ResolutionStep.Yield(
        SolutionPresentation.Halt(queryText, exception.message ?: "Resolution halted", exception.logicStackTrace.map { it.toString() }),
        hasMorePotentially = false,
    )
}
