package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.SolverSessionLifecycle
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultGuiControllerTest {
    private fun fixture(scope: CoroutineScope): Pair<DefaultGuiController, TestSolverFactory> {
        val factory = TestSolverFactory()
        val controller =
            DefaultGuiController(
                workspaceConfiguration = WorkspaceConfiguration(defaultSolverProfileId = testProfileId),
                baseProfiles = listOf(factory.profile),
                parentScope = scope,
            )
        return controller to factory
    }

    @Test
    fun pageQueriesAndResolutionsAreIsolated() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1).\np(2)."))
                val pageA = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageA, "p(X)."))

                controller.dispatch(WorkspaceAction.NewDocumentPage("b.pl", "q(a)."))
                val pageB = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageB, "q(Y)."))

                assertNotEquals(pageA, pageB)
                assertEquals(
                    "p(X).",
                    controller.state.value.workspace
                        .page(pageA)
                        ?.query
                        ?.text,
                )
                assertEquals(
                    "q(Y).",
                    controller.state.value.workspace
                        .page(pageB)
                        ?.query
                        ?.text,
                )

                controller.dispatch(PageAction.Solve(pageA))
                val suspendedA = controller.awaitResolution(pageA, ResolutionStatus.AWAITING_CONTINUATION)
                assertEquals(1, suspendedA.resolution.solutions.size)
                assertEquals(
                    ResolutionStatus.IDLE,
                    controller.state.value.workspace
                        .page(pageB)
                        ?.resolution
                        ?.status,
                )

                controller.dispatch(WorkspaceAction.SelectPage(pageB))
                controller.dispatch(PageAction.Solve(pageB, ConsumptionMode.ALL))
                val completedB = controller.awaitResolution(pageB, ResolutionStatus.COMPLETED)
                assertEquals(1, completedB.resolution.solutions.size)
                assertEquals(
                    "p(X).",
                    controller.state.value.workspace
                        .page(pageA)
                        ?.query
                        ?.text,
                )

                controller.dispatch(PageAction.Next(pageA, ConsumptionMode.ALL))
                val completedA = controller.awaitResolution(pageA, ResolutionStatus.COMPLETED)
                assertEquals(2, completedA.resolution.solutions.size)
                assertEquals(pageB, controller.state.value.workspace.selectedPageId)
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun backgroundPageResultNeverMutatesSelectedPage() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("slow.pl"))
                val pageA = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageA, "slow(X)."))
                controller.dispatch(PageAction.Solve(pageA))

                controller.dispatch(WorkspaceAction.NewDocumentPage("foreground.pl"))
                val pageB = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageB, "q(Y)."))

                val completedA = controller.awaitResolution(pageA, ResolutionStatus.COMPLETED)
                assertEquals(1, completedA.resolution.solutions.size)
                val foreground =
                    controller.state.value.workspace
                        .page(pageB)!!
                assertEquals("q(Y).", foreground.query.text)
                assertEquals(ResolutionStatus.IDLE, foreground.resolution.status)
                assertTrue(foreground.resolution.solutions.isEmpty())
                assertEquals(pageB, controller.state.value.workspace.selectedPageId)
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun stoppedResolutionCannotPublishALateResult() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("slow.pl"))
                val page = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(page, "slow(X)."))
                controller.dispatch(PageAction.Solve(page))
                controller.awaitResolution(page, ResolutionStatus.RUNNING)
                controller.dispatch(PageAction.Stop(page))
                controller.dispatch(PageAction.ChangeQuery(page, "p(X)."))

                delay(250)
                val stopped =
                    controller.state.value.workspace
                        .page(page)!!
                assertEquals(ResolutionStatus.IDLE, stopped.resolution.status)
                assertTrue(stopped.resolution.solutions.isEmpty())

                controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
                val completed = controller.awaitResolution(page, ResolutionStatus.COMPLETED)
                assertEquals(2, completed.resolution.solutions.size)
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun changingSharedDocumentInvalidatesEveryReferencingPage() =
        runTest {
            val (controller, factory) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("shared.pl", "p(1)."))
                val pageA = controller.state.value.workspace.selectedPageId!!
                val document =
                    (
                        controller.state.value.workspace
                            .page(
                                pageA,
                            )?.content as PageContent.DocumentReference
                    ).documentId
                controller.dispatch(PageAction.ChangeQuery(pageA, "p(X)."))
                controller.dispatch(PageAction.Solve(pageA, ConsumptionMode.ALL))
                controller.awaitResolution(pageA, ResolutionStatus.COMPLETED)

                controller.dispatch(WorkspaceAction.NewPageForDocument(document, "second view"))
                val pageB = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageB, "p(X)."))
                controller.dispatch(PageAction.Solve(pageB, ConsumptionMode.ALL))
                controller.awaitResolution(pageB, ResolutionStatus.COMPLETED)
                assertEquals(2, factory.creationRequests.size)

                controller.dispatch(DocumentAction.ChangeText(document, "p(1).\np(2)."))
                val state = controller.state.value.workspace
                assertEquals(SolverSessionLifecycle.STALE, state.page(pageA)?.solverSession?.lifecycle)
                assertEquals(SolverSessionLifecycle.STALE, state.page(pageB)?.solverSession?.lifecycle)
                assertEquals(ResolutionStatus.IDLE, state.page(pageA)?.resolution?.status)
                assertEquals(ResolutionStatus.IDLE, state.page(pageB)?.resolution?.status)
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun dirtyCloseAndReloadAreExplicitPlatformEffects() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.OpenDocumentLoaded(DocumentOrigin("test", "a", "a.pl"), "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                val document =
                    (
                        controller.state.value.workspace
                            .page(
                                page,
                            )?.content as PageContent.DocumentReference
                    ).documentId
                controller.dispatch(DocumentAction.ChangeText(document, "p(2)."))

                val closeEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                assertIs<GuiEffect.ConfirmCloseDirtyPage>(closeEffect.await())
                assertNotNull(
                    controller.state.value.workspace
                        .page(page),
                )

                val reloadEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.RequestReload(document))
                assertIs<GuiEffect.ConfirmReloadDirtyDocument>(reloadEffect.await())

                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.DISCARD))
                assertNull(
                    controller.state.value.workspace
                        .page(page),
                )
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun saveThenCloseDoesNotLoseAnEditMadeDuringTheWrite() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("race.pl", "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                val document =
                    (
                        controller.state.value.workspace
                            .page(
                                page,
                            )?.content as PageContent.DocumentReference
                    ).documentId

                val closeEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                assertIs<GuiEffect.ConfirmCloseDirtyPage>(closeEffect.await())

                val destinationEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
                assertIs<GuiEffect.PickSaveDestination>(destinationEffect.await())

                val origin = DocumentOrigin("test", "race", "race.pl")
                val writeEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                val write = assertIs<GuiEffect.WriteDocument>(writeEffect.await())
                assertEquals(1, write.revision)

                controller.dispatch(DocumentAction.ChangeText(document, "p(2)."))
                controller.dispatch(DocumentAction.SaveSucceeded(document, origin, savedRevision = write.revision))

                val state = controller.state.value.workspace
                assertNotNull(state.page(page))
                assertTrue(state.document(document)!!.isDirty)
                assertEquals("p(2).", state.document(document)?.text)
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun producedSolutionsRemainUnreadUntilTheSolutionsPanelIsAcknowledged() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("solutions.pl", "p(1)."))
                val pageId = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageId, "p(X)."))
                controller.dispatch(PageAction.Solve(pageId, ConsumptionMode.ALL))
                val completed = controller.awaitResolution(pageId, ResolutionStatus.COMPLETED)
                assertTrue(completed.resolution.hasUnreadChanges)

                controller.dispatch(PageAction.MarkPanelRead(pageId, PanelId.SOLUTIONS))
                assertFalse(
                    controller.state.value.workspace
                        .page(pageId)!!
                        .resolution.hasUnreadChanges,
                )
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun cancelledNonCooperativeFailureCannotOverwriteTerminalState() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("slow-fail.pl"))
                val page = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(page, "slowFail(X)."))
                controller.dispatch(PageAction.Solve(page))
                controller.awaitResolution(page, ResolutionStatus.RUNNING)
                controller.dispatch(PageAction.Stop(page))
                controller.awaitResolution(page, ResolutionStatus.CANCELLED)

                delay(250)
                val resolution =
                    controller.state.value.workspace
                        .page(page)!!
                        .resolution
                assertEquals(ResolutionStatus.CANCELLED, resolution.status)
                assertNull(resolution.error)
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun cancelledSaveBeforeCloseDoesNotClosePageAfterALaterSave() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("cancel-save.pl", "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                val document =
                    (
                        controller.state.value.workspace
                            .page(
                                page,
                            )?.content as PageContent.DocumentReference
                    ).documentId

                val closeEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                assertIs<GuiEffect.ConfirmCloseDirtyPage>(closeEffect.await())
                val destinationEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
                assertIs<GuiEffect.PickSaveDestination>(destinationEffect.await())
                controller.dispatch(DocumentAction.SaveCancelled(document))

                val origin = DocumentOrigin("test", "cancel-save", "cancel-save.pl")
                val writeEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                val write = assertIs<GuiEffect.WriteDocument>(writeEffect.await())
                controller.dispatch(DocumentAction.SaveSucceeded(document, origin, write.revision))

                assertNotNull(
                    controller.state.value.workspace
                        .page(page),
                )
                assertFalse(
                    controller.state.value.workspace
                        .document(document)!!
                        .isDirty,
                )
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun failedSaveBeforeCloseDoesNotClosePageAfterALaterSave() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("failed-save.pl", "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                val document =
                    (
                        controller.state.value.workspace
                            .page(
                                page,
                            )?.content as PageContent.DocumentReference
                    ).documentId

                val closeEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                assertIs<GuiEffect.ConfirmCloseDirtyPage>(closeEffect.await())
                val destinationEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
                assertIs<GuiEffect.PickSaveDestination>(destinationEffect.await())
                controller.dispatch(DocumentAction.SaveFailed(document, "disk full"))

                val origin = DocumentOrigin("test", "failed-save", "failed-save.pl")
                val writeEffect =
                    async(start = CoroutineStart.UNDISPATCHED) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                val write = assertIs<GuiEffect.WriteDocument>(writeEffect.await())
                controller.dispatch(DocumentAction.SaveSucceeded(document, origin, write.revision))

                assertNotNull(
                    controller.state.value.workspace
                        .page(page),
                )
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun changingAwaitingQueryRecordsCancellationHistory() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("history.pl", "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(page, "p(X)."))
                controller.dispatch(PageAction.Solve(page, ConsumptionMode.ONE))
                controller.awaitResolution(page, ResolutionStatus.AWAITING_CONTINUATION)

                controller.dispatch(PageAction.ChangeQuery(page, "q(Y)."))
                val state =
                    controller.state.value.workspace
                        .page(page)!!
                assertEquals(ResolutionStatus.IDLE, state.resolution.status)
                assertEquals("q(Y).", state.query.text)
                assertEquals(1, state.history.resolutions.size)
                assertEquals(
                    ResolutionStatus.CANCELLED,
                    state.history.resolutions
                        .single()
                        .terminalStatus,
                )
                assertEquals(
                    "p(X).",
                    state.history.resolutions
                        .single()
                        .query,
                )
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun staleSaveCompletionCannotRegressPersistedRevision() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("ordered-save.pl", "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                val document =
                    (
                        controller.state.value.workspace
                            .page(
                                page,
                            )?.content as PageContent.DocumentReference
                    ).documentId
                val first = DocumentOrigin("test", "first", "first.pl")
                controller.dispatch(DocumentAction.SaveSucceeded(document, first, savedRevision = 1))
                controller.dispatch(DocumentAction.ChangeText(document, "p(2)."))
                val second = DocumentOrigin("test", "second", "second.pl")
                controller.dispatch(DocumentAction.SaveSucceeded(document, second, savedRevision = 2))
                controller.dispatch(DocumentAction.SaveSucceeded(document, first, savedRevision = 1))

                val saved =
                    controller.state.value.workspace
                        .document(document)!!
                assertEquals(2L, saved.persistedRevision)
                assertEquals(second, saved.origin)
                assertFalse(saved.isDirty)
            } finally {
                controller.shutdown()
            }
        }

    @Test
    fun scratchPageHasNoDocumentAndRetainsPageSpecificQuery() =
        runTest {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewScratchPage("scratch", "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(page, "p(X)."))
                val state = controller.state.value.workspace
                assertIs<PageContent.Scratch>(state.page(page)?.content)
                assertTrue(state.documents.isEmpty())
                assertEquals("p(X).", state.page(page)?.query?.text)
                assertFalse(state.page(page)?.title.isNullOrBlank())
            } finally {
                controller.shutdown()
            }
        }
}
