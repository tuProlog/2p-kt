package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionCursor
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.ResolutionStep
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile
import it.unibo.tuprolog.ui.gui.solver.SolverSession
import it.unibo.tuprolog.ui.gui.solver.SolverSignal
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

internal val testProfileId = SolverProfileId("test")

internal class TestSolverFactory {
    private var counter: Int = 0
    val creationRequests = mutableListOf<it.unibo.tuprolog.ui.gui.solver.SolverSessionCreationRequest>()

    val profile =
        SolverProfile(
            id = testProfileId,
            displayName = "Test solver",
            capabilities =
                SolverCapabilities(
                    setOf(
                        SolverCapabilities.CANCELLATION,
                        SolverCapabilities.FLAGS_INSPECTION,
                    ),
                ),
            factory = { request ->
                creationRequests += request
                TestSolverSession(SolverSessionId("test-session-${++counter}"))
            },
        )
}

internal class TestSolverSession(
    override val id: SolverSessionId,
) : SolverSession {
    override val capabilities =
        SolverCapabilities(
            setOf(
                SolverCapabilities.CANCELLATION,
                SolverCapabilities.FLAGS_INSPECTION,
            ),
        )

    override val snapshot =
        SolverInspectionSnapshot(
            flags = listOf(FlagPresentation("test", "true")),
        )

    override suspend fun openResolution(request: ResolutionRequest): ResolutionCursor = TestResolutionCursor(request)

    override suspend fun reset(): SolverInspectionSnapshot = snapshot

    override suspend fun close() = Unit
}

internal class TestResolutionCursor(
    private val request: ResolutionRequest,
) : ResolutionCursor {
    private var index = 0
    private var cancelled = false

    override suspend fun next(): ResolutionStep {
        val step = index++
        return when (request.query) {
            "p(X)." ->
                when (step) {
                    0 ->
                        ResolutionStep.Yield(
                            solution = yes("X", "1"),
                            hasMorePotentially = true,
                        )
                    1 ->
                        ResolutionStep.Yield(
                            solution = yes("X", "2"),
                            hasMorePotentially = false,
                        )
                    else -> ResolutionStep.End()
                }
            "q(Y)." ->
                if (step == 0) {
                    ResolutionStep.Yield(yes("Y", "a"), hasMorePotentially = false)
                } else {
                    ResolutionStep.End()
                }
            "output." ->
                if (step == 0) {
                    ResolutionStep.Yield(
                        solution = SolutionPresentation.Yes(request.query),
                        hasMorePotentially = false,
                        signals =
                            listOf(
                                SolverSignal.Stdout("hello\n"),
                                SolverSignal.Warning(WarningPresentation("careful")),
                            ),
                    )
                } else {
                    ResolutionStep.End()
                }
            "slow(X)." -> {
                // Deliberately ignores cooperative cancellation while sleeping. The controller must still
                // reject this result after Stop, source invalidation, or a newer resolution.
                withContext(NonCancellable) { delay(150) }
                ResolutionStep.Yield(yes("X", "stale"), hasMorePotentially = false)
            }
            "slowFail(X)." -> {
                // Deliberately throws after cancellation to verify terminal-state protection.
                withContext(NonCancellable) { delay(150) }
                error("late non-cooperative failure")
            }
            else ->
                if (step == 0) {
                    ResolutionStep.Yield(SolutionPresentation.No(request.query), hasMorePotentially = false)
                } else {
                    ResolutionStep.End()
                }
        }
    }

    override suspend fun cancel() {
        cancelled = true
    }

    private fun yes(
        variable: String,
        value: String,
    ): SolutionPresentation.Yes =
        SolutionPresentation.Yes(
            query = request.query,
            bindings = listOf(BindingPresentation(variable, value)),
        )
}

internal suspend fun GuiController.awaitPage(
    pageId: PageId,
    predicate: (PageState) -> Boolean,
): PageState =
    withTimeout(5_000) {
        state
            .first { snapshot ->
                snapshot.workspace.page(pageId)?.let(predicate) == true
            }.workspace
            .page(pageId)!!
    }

internal suspend fun GuiController.awaitResolution(
    pageId: PageId,
    status: ResolutionStatus,
): PageState = awaitPage(pageId) { it.resolution.status == status }
