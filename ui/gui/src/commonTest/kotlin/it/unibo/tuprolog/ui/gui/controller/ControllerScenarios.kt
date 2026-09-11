package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.extension.GuiContributions
import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.PageConfiguration
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.SolverSessionLifecycle
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.testing.ScriptedSolverFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield

object ControllerScenarios {
    suspend fun anExtensionMayProvideTheOnlySolverProfile() =
        coroutineScope {
            val factory = ScriptedSolverFactory()
            val extension =
                object : GuiExtension {
                    override val id = ExtensionId("solver-only-extension")
                    override val contributions = GuiContributions(solverProfiles = listOf(factory.profile))
                }
            val application =
                buildGuiApplication(this) {
                    extension(extension)
                    defaultSolverProfile(factory.profileId)
                }
            try {
                application.start()
                application.controller.dispatch(WorkspaceAction.NewDocumentPage("extension.pl", "value(1)."))
                val page = application.controller.state.value.workspace.selectedPageId ?: error("No selected page")
                application.controller.dispatch(PageAction.ChangeQuery(page, "one."))
                application.controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
                val reached =
                    withTimeoutOrNull(3_000) {
                        while (application.controller.state.value.workspace
                                .page(page)
                                ?.resolution
                                ?.status !=
                            ResolutionStatus.COMPLETED
                        ) {
                            delay(5)
                        }
                        true
                    } ?: false
                check(reached)
                check(factory.creationRequests.size == 1)
            } finally {
                application.close()
            }
        }

    suspend fun solverProfileDefaultsParticipateInEffectiveOptions() =
        coroutineScope {
            val factory =
                ScriptedSolverFactory(
                    profileDefaultOptions =
                        mapOf(
                            "dialect" to "strict",
                            "trace" to "off",
                            "profile-only" to "yes",
                        ),
                )
            val controller =
                DefaultGuiController(
                    workspaceConfiguration =
                        WorkspaceConfiguration(
                            defaultSolverProfileId = factory.profileId,
                            defaultOptions =
                                mapOf(
                                    "trace" to "on",
                                    "workspace-only" to "yes",
                                ),
                        ),
                    baseProfiles = listOf(factory.profile),
                    parentScope = this,
                )
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("options.pl", "value(1)."))
                val page = controller.state.value.workspace.selectedPageId ?: error("No selected page")
                controller.dispatch(
                    PageAction.ChangeConfiguration(
                        page,
                        PageConfiguration(
                            optionOverrides =
                                mapOf(
                                    "dialect" to "custom",
                                    "page-only" to "yes",
                                ),
                        ),
                    ),
                )
                controller.dispatch(PageAction.ChangeQuery(page, "one."))
                controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
                val reached =
                    withTimeoutOrNull(3_000) {
                        while (controller.state.value.workspace
                                .page(page)
                                ?.resolution
                                ?.status !=
                            ResolutionStatus.COMPLETED
                        ) {
                            delay(5)
                        }
                        true
                    } ?: false
                check(reached)
                check(
                    factory.creationRequests.single().options ==
                        mapOf(
                            "dialect" to "custom",
                            "trace" to "on",
                            "profile-only" to "yes",
                            "workspace-only" to "yes",
                            "page-only" to "yes",
                        ),
                )
            } finally {
                controller.shutdown()
            }
        }

    suspend fun pageSpecificQueryAndResultsAreIsolated() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "a."))
            val pageA = fixture.selectedPageId()
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("b.pl", "b."))
            val pageB = fixture.selectedPageId()

            fixture.controller.dispatch(PageAction.ChangeQuery(pageA, "page-a."))
            fixture.controller.dispatch(PageAction.ChangeQuery(pageB, "page-b."))
            fixture.controller.dispatch(PageAction.Solve(pageA))
            fixture.controller.dispatch(PageAction.Solve(pageB))

            fixture.awaitResolution(pageA, ResolutionStatus.COMPLETED)
            fixture.awaitResolution(pageB, ResolutionStatus.COMPLETED)

            val state = fixture.controller.state.value.workspace
            check(state.page(pageA)?.query?.text == "page-a.")
            check(state.page(pageB)?.query?.text == "page-b.")
            val solutionA =
                requireNotNull(
                    state
                        .page(pageA)
                        ?.resolution
                        ?.solutions
                        ?.singleOrNull(),
                )
            val solutionB =
                requireNotNull(
                    state
                        .page(pageB)
                        ?.resolution
                        ?.solutions
                        ?.singleOrNull(),
                )
            check(solutionA.binding("Page") == "A")
            check(solutionB.binding("Page") == "B")

            fixture.controller.dispatch(WorkspaceAction.SelectPage(pageA))
            check(fixture.controller.state.value.workspace.selectedPageId == pageA)
            check(
                fixture.controller.state.value.workspace
                    .page(pageB)
                    ?.query
                    ?.text == "page-b.",
            )
        }
    }

    suspend fun solverSessionsAreLazyReusedAndRevisionAware() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("reuse.pl", "value(1)."))
            val page = fixture.selectedPageId()
            val document = fixture.documentId(page)
            check(fixture.factory.creationRequests.isEmpty())

            fixture.controller.dispatch(PageAction.ChangeQuery(page, "one."))
            fixture.controller.dispatch(PageAction.Solve(page))
            fixture.awaitResolution(page, ResolutionStatus.COMPLETED)
            check(fixture.factory.creationRequests.size == 1)
            check(
                fixture.controller.state.value.workspace
                    .page(page)
                    ?.solverSession
                    ?.lifecycle ==
                    SolverSessionLifecycle.FRESH,
            )

            fixture.controller.dispatch(PageAction.ChangeQuery(page, "two."))
            fixture.controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
            fixture.awaitResolution(page, ResolutionStatus.COMPLETED)
            check(
                fixture.factory.creationRequests.size == 1,
            ) { "Unchanged source/configuration must reuse the page session" }

            fixture.controller.dispatch(DocumentAction.ChangeText(document, "value(2)."))
            val invalidated =
                fixture.controller.state.value.workspace
                    .page(page) ?: error("page disappeared")
            check(invalidated.solverSession.lifecycle == SolverSessionLifecycle.STALE)
            check(fixture.factory.closedSessions.size == 1)

            fixture.controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
            fixture.awaitResolution(page, ResolutionStatus.COMPLETED)
            check(fixture.factory.creationRequests.size == 2)
            check(
                fixture.factory.creationRequests
                    .last()
                    .documentRevision == 2L,
            )
        }
    }

    suspend fun allPagesSharingADocumentAreInvalidated() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("shared.pl", "value(1)."))
            val firstPage = fixture.selectedPageId()
            val document = fixture.documentId(firstPage)
            fixture.controller.dispatch(WorkspaceAction.NewPageForDocument(document, "shared second view"))
            val secondPage = fixture.selectedPageId()

            fixture.controller.dispatch(PageAction.ChangeQuery(firstPage, "one."))
            fixture.controller.dispatch(PageAction.ChangeQuery(secondPage, "one."))
            fixture.controller.dispatch(PageAction.Solve(firstPage))
            fixture.controller.dispatch(PageAction.Solve(secondPage))
            fixture.awaitResolution(firstPage, ResolutionStatus.COMPLETED)
            fixture.awaitResolution(secondPage, ResolutionStatus.COMPLETED)
            check(fixture.factory.creationRequests.size == 2)

            fixture.controller.dispatch(DocumentAction.ChangeText(document, "value(2)."))
            val workspace = fixture.controller.state.value.workspace
            check(workspace.page(firstPage)?.solverSession?.lifecycle == SolverSessionLifecycle.STALE)
            check(workspace.page(secondPage)?.solverSession?.lifecycle == SolverSessionLifecycle.STALE)
            check(fixture.factory.closedSessions.size == 2)
        }
    }

    suspend fun cancelledResolutionCannotPublishALateResult() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("slow.pl", "slow_source."))
            val page = fixture.selectedPageId()
            fixture.controller.dispatch(PageAction.ChangeQuery(page, "slow."))
            fixture.controller.dispatch(PageAction.Solve(page))
            fixture.awaitResolution(page, ResolutionStatus.RUNNING)

            fixture.controller.dispatch(PageAction.Stop(page))
            fixture.awaitResolution(page, ResolutionStatus.CANCELLED)
            delay(220)

            val resolution =
                fixture.controller.state.value.workspace
                    .page(page)
                    ?.resolution ?: error("page disappeared")
            check(resolution.status == ResolutionStatus.CANCELLED)
            check(
                resolution.solutions.isEmpty(),
            ) { "A result from a cancelled resolution leaked into observable state" }
            check(page in fixture.factory.cancelledPages)
        }
    }

    suspend fun dirtyDocumentSaveUsesEffectsAndClearsDirtyStateOnlyAfterSuccess() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("save.pl", ""))
            val page = fixture.selectedPageId()
            val document = fixture.documentId(page)
            fixture.controller.dispatch(DocumentAction.ChangeText(document, "saved(value)."))
            check(fixture.document(document).isDirty)

            val pick =
                fixture.captureEffect<GuiEffect.PickSaveDestination> {
                    fixture.controller.dispatch(DocumentAction.RequestSave(document))
                }
            check(pick.documentId == document)

            val origin = DocumentOrigin("test", "/tmp/save.pl", "save.pl")
            val write =
                fixture.captureEffect<GuiEffect.WriteDocument> {
                    fixture.controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                }
            check(write.documentId == document)
            check(write.revision == 1L)
            check(fixture.document(document).isDirty)

            fixture.controller.dispatch(DocumentAction.SaveSucceeded(document, origin, write.revision))
            check(!fixture.document(document).isDirty)
            check(fixture.document(document).persistedRevision == 1L)
        }
    }

    suspend fun cancelledNonCooperativeFailureCannotOverwriteTerminalState() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("slow-fail.pl", "slow_source."))
            val page = fixture.selectedPageId()
            fixture.controller.dispatch(PageAction.ChangeQuery(page, "slow-fail."))
            fixture.controller.dispatch(PageAction.Solve(page))
            fixture.awaitResolution(page, ResolutionStatus.RUNNING)

            fixture.controller.dispatch(PageAction.Stop(page))
            fixture.awaitResolution(page, ResolutionStatus.CANCELLED)
            delay(220)

            val resolution =
                fixture.controller.state.value.workspace
                    .page(page)
                    ?.resolution ?: error("page disappeared")
            check(resolution.status == ResolutionStatus.CANCELLED)
            check(resolution.error == null) { "A late backend failure overwrote cancelled state" }
        }
    }

    suspend fun saveCancellationClearsPendingCloseIntent() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("cancel-save.pl", "value(1)."))
            val page = fixture.selectedPageId()
            val document = fixture.documentId(page)

            fixture.captureEffect<GuiEffect.ConfirmCloseDirtyPage> {
                fixture.controller.dispatch(WorkspaceAction.RequestClosePage(page))
            }
            fixture.captureEffect<GuiEffect.PickSaveDestination> {
                fixture.controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
            }
            fixture.controller.dispatch(DocumentAction.SaveCancelled(document))

            val origin = DocumentOrigin("test", "/tmp/cancel-save.pl", "cancel-save.pl")
            val write =
                fixture.captureEffect<GuiEffect.WriteDocument> {
                    fixture.controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                }
            fixture.controller.dispatch(DocumentAction.SaveSucceeded(document, origin, write.revision))

            check(
                fixture.controller.state.value.workspace
                    .page(page) != null,
            ) {
                "A later ordinary save unexpectedly completed a cancelled save-before-close operation"
            }
            check(!fixture.document(document).isDirty)
        }
    }

    suspend fun saveFailureClearsPendingCloseIntent() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("failed-save.pl", "value(1)."))
            val page = fixture.selectedPageId()
            val document = fixture.documentId(page)

            fixture.captureEffect<GuiEffect.ConfirmCloseDirtyPage> {
                fixture.controller.dispatch(WorkspaceAction.RequestClosePage(page))
            }
            fixture.captureEffect<GuiEffect.PickSaveDestination> {
                fixture.controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
            }
            fixture.controller.dispatch(DocumentAction.SaveFailed(document, "disk full"))

            val origin = DocumentOrigin("test", "/tmp/failed-save.pl", "failed-save.pl")
            val write =
                fixture.captureEffect<GuiEffect.WriteDocument> {
                    fixture.controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                }
            fixture.controller.dispatch(DocumentAction.SaveSucceeded(document, origin, write.revision))

            check(
                fixture.controller.state.value.workspace
                    .page(page) != null,
            ) {
                "A later ordinary save unexpectedly completed a failed save-before-close operation"
            }
        }
    }

    suspend fun changingAnAwaitingQueryRecordsCancellationHistory() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("history.pl", "two_source."))
            val page = fixture.selectedPageId()
            fixture.controller.dispatch(PageAction.ChangeQuery(page, "two."))
            fixture.controller.dispatch(PageAction.Solve(page, ConsumptionMode.ONE))
            fixture.awaitResolution(page, ResolutionStatus.AWAITING_CONTINUATION)

            fixture.controller.dispatch(PageAction.ChangeQuery(page, "one."))
            val state =
                fixture.controller.state.value.workspace
                    .page(page) ?: error("page disappeared")
            check(state.resolution.status == ResolutionStatus.IDLE)
            check(state.query.text == "one.")
            val history = state.history.resolutions.single()
            check(history.query == "two.")
            check(history.terminalStatus == ResolutionStatus.CANCELLED)
            check(history.solutions.size == 1)
        }
    }

    suspend fun staleSaveCompletionCannotRegressPersistedRevision() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("ordered-save.pl", "value(1)."))
            val page = fixture.selectedPageId()
            val document = fixture.documentId(page)
            val firstOrigin = DocumentOrigin("test", "/tmp/first.pl", "first.pl")
            fixture.controller.dispatch(DocumentAction.SaveSucceeded(document, firstOrigin, savedRevision = 1))

            fixture.controller.dispatch(DocumentAction.ChangeText(document, "value(2)."))
            val secondOrigin = DocumentOrigin("test", "/tmp/second.pl", "second.pl")
            fixture.controller.dispatch(DocumentAction.SaveSucceeded(document, secondOrigin, savedRevision = 2))
            fixture.controller.dispatch(DocumentAction.SaveSucceeded(document, firstOrigin, savedRevision = 1))

            val saved = fixture.document(document)
            check(saved.persistedRevision == 2L)
            check(saved.origin == secondOrigin)
            check(!saved.isDirty)
        }
    }

    suspend fun oneAtATimeContinuationPreservesResolutionIdentity() {
        withFixture { fixture ->
            fixture.controller.dispatch(WorkspaceAction.NewDocumentPage("two.pl", "two_source."))
            val page = fixture.selectedPageId()
            fixture.controller.dispatch(PageAction.ChangeQuery(page, "two."))
            fixture.controller.dispatch(PageAction.Solve(page, ConsumptionMode.ONE))
            fixture.awaitResolution(page, ResolutionStatus.AWAITING_CONTINUATION)
            val first =
                fixture.controller.state.value.workspace
                    .page(page)
                    ?.resolution ?: error("page disappeared")
            check(first.solutions.size == 1)
            val resolutionId = first.id

            fixture.controller.dispatch(PageAction.Next(page, ConsumptionMode.ONE))
            fixture.awaitResolution(page, ResolutionStatus.COMPLETED)
            val completed =
                fixture.controller.state.value.workspace
                    .page(page)
                    ?.resolution ?: error("page disappeared")
            check(completed.id == resolutionId)
            check(completed.solutions.map { it.binding("Result") } == listOf("first", "second"))
            check(
                fixture.controller.state.value.workspace
                    .page(page)
                    ?.history
                    ?.resolutions
                    ?.single()
                    ?.query == "two.",
            )
        }
    }

    private suspend fun <T> withFixture(block: suspend (Fixture) -> T): T {
        val fixture = Fixture()
        return try {
            block(fixture)
        } finally {
            fixture.close()
        }
    }

    private class Fixture {
        val factory = ScriptedSolverFactory()
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val controller =
            DefaultGuiController(
                workspaceConfiguration = WorkspaceConfiguration(factory.profileId),
                baseProfiles = listOf(factory.profile),
                parentScope = scope,
            )

        fun selectedPageId(): PageId = controller.state.value.workspace.selectedPageId ?: error("No selected page")

        fun documentId(pageId: PageId): DocumentId {
            val page =
                controller.state.value.workspace
                    .page(pageId) ?: error("Missing page $pageId")
            return (page.content as? PageContent.DocumentReference)?.documentId ?: error("Page is not document-backed")
        }

        fun document(documentId: DocumentId) =
            controller.state.value.workspace
                .document(documentId) ?: error("Missing document $documentId")

        suspend fun awaitResolution(
            pageId: PageId,
            status: ResolutionStatus,
        ) {
            val reached =
                withTimeoutOrNull(3_000) {
                    while (controller.state.value.workspace
                            .page(pageId)
                            ?.resolution
                            ?.status != status
                    ) {
                        delay(5)
                    }
                    true
                } ?: false
            if (!reached) {
                error(
                    "Timed out waiting for $status on $pageId; current page state=" +
                        controller.state.value.workspace
                            .page(pageId),
                )
            }
        }

        suspend inline fun <reified T : GuiEffect> captureEffect(crossinline action: suspend () -> Unit): T =
            coroutineScope {
                val pending = async { controller.effects.first { it is T } as T }
                yield()
                action()
                withTimeout(3_000) { pending.await() }
            }

        suspend fun close() {
            controller.shutdown()
        }
    }

    private fun SolutionPresentation.binding(variable: String): String? =
        (this as? SolutionPresentation.Yes)?.bindings?.firstOrNull { it.variable == variable }?.value
}
