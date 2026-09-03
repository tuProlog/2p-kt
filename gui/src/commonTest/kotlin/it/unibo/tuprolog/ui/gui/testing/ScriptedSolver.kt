package it.unibo.tuprolog.ui.gui.testing

import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ScriptedSolverFactory(
    val profileId: SolverProfileId = SolverProfileId("scripted"),
    private val profileDefaultOptions: Map<String, String> = emptyMap(),
) {
    val creationRequests: MutableList<SolverSessionCreationRequest> = mutableListOf()
    val openedResolutions: MutableList<ResolutionRequest> = mutableListOf()
    val closedSessions: MutableList<SolverSessionId> = mutableListOf()
    val cancelledPages: MutableList<PageId> = mutableListOf()

    val profile: SolverProfile =
        SolverProfile(
            id = profileId,
            displayName = "Scripted test solver",
            capabilities =
                SolverCapabilities(
                    setOf(
                        SolverCapabilities.CANCELLATION,
                        SolverCapabilities.STATIC_KB_INSPECTION,
                    ),
                ),
            factory = { request ->
                creationRequests += request
                ScriptedSession(
                    id = SolverSessionId("session-${creationRequests.size}"),
                    factory = this,
                )
            },
            defaultOptions = profileDefaultOptions,
        )

    private class ScriptedSession(
        override val id: SolverSessionId,
        private val factory: ScriptedSolverFactory,
    ) : SolverSession {
        override val capabilities: SolverCapabilities = factory.profile.capabilities
        override val snapshot: SolverInspectionSnapshot = SolverInspectionSnapshot(staticKnowledgeBase = "snapshot:$id")

        override suspend fun openResolution(request: ResolutionRequest): ResolutionCursor {
            factory.openedResolutions += request
            return ScriptedCursor(request, factory)
        }

        override suspend fun reset(): SolverInspectionSnapshot = snapshot

        override suspend fun close() {
            factory.closedSessions += id
        }
    }

    private class ScriptedCursor(
        private val request: ResolutionRequest,
        private val factory: ScriptedSolverFactory,
    ) : ResolutionCursor {
        private var index: Int = 0
        private var cancelled: Boolean = false

        override suspend fun next(): ResolutionStep {
            val step = index++
            return when (request.query) {
                "one." ->
                    ResolutionStep.Yield(
                        solution = yes(request.query, "Result", "one"),
                        hasMorePotentially = false,
                    )
                "two." ->
                    if (step == 0) {
                        ResolutionStep.Yield(
                            solution = yes(request.query, "Result", "first"),
                            hasMorePotentially = true,
                        )
                    } else {
                        ResolutionStep.Yield(
                            solution = yes(request.query, "Result", "second"),
                            hasMorePotentially = false,
                        )
                    }
                "page-a." ->
                    ResolutionStep.Yield(
                        solution = yes(request.query, "Page", "A"),
                        hasMorePotentially = false,
                    )
                "page-b." ->
                    ResolutionStep.Yield(
                        solution = yes(request.query, "Page", "B"),
                        hasMorePotentially = false,
                    )
                "slow." -> {
                    // Intentionally ignore coroutine cancellation long enough to exercise stale-result rejection.
                    withContext(NonCancellable) { delay(120) }
                    ResolutionStep.Yield(
                        solution = yes(request.query, "Late", "result"),
                        hasMorePotentially = false,
                    )
                }
                "slow-fail." -> {
                    // A cancelled, non-cooperative backend may still throw. Its late failure must be ignored.
                    withContext(NonCancellable) { delay(120) }
                    error("late scripted failure")
                }
                "fail." -> ResolutionStep.Failed("scripted failure")
                else -> ResolutionStep.End()
            }
        }

        override suspend fun cancel() {
            cancelled = true
            factory.cancelledPages += request.pageId
        }

        @Suppress("unused")
        fun isCancelled(): Boolean = cancelled
    }

    companion object {
        private fun yes(
            query: String,
            variable: String,
            value: String,
        ): SolutionPresentation.Yes =
            SolutionPresentation.Yes(
                query = query,
                bindings = listOf(BindingPresentation(variable, value)),
            )
    }
}
