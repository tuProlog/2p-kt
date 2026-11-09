package it.unibo.tuprolog.ui.gui.controller

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.SolverSessionLifecycle
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSources
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

class DefaultGuiControllerSpec :
    FunSpec({
        fun fixture(scope: CoroutineScope): Pair<DefaultGuiController, TestSolverFactory> {
            val factory = TestSolverFactory()
            val controller =
                DefaultGuiController(
                    workspaceConfiguration = WorkspaceConfiguration(defaultSolverProfileId = testProfileId),
                    baseProfiles = listOf(factory.profile),
                    parentScope = scope,
                )
            return controller to factory
        }

        test("page-queries-and-resolutions-are-isolated").config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1).\np(2)."))
                val pageA = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageA, "p(X)."))

                controller.dispatch(WorkspaceAction.NewDocumentPage("b.pl", "q(a)."))
                val pageB = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageB, "q(Y)."))

                pageB shouldNotBe pageA
                (
                    controller.state.value.workspace
                        .page(pageA)
                        ?.query
                        ?.text
                ) shouldBe "p(X)."
                (
                    controller.state.value.workspace
                        .page(pageB)
                        ?.query
                        ?.text
                ) shouldBe "q(Y)."

                controller.dispatch(PageAction.Solve(pageA))
                val suspendedA = controller.awaitResolution(pageA, ResolutionStatus.AWAITING_CONTINUATION)
                suspendedA.resolution.solutions.size shouldBe 1
                val submittedAt = suspendedA.resolution.submittedAt.shouldNotBeNull()
                (
                    controller.state.value.workspace
                        .page(pageB)
                        ?.resolution
                        ?.status
                ) shouldBe ResolutionStatus.IDLE

                controller.dispatch(WorkspaceAction.SelectPage(pageB))
                controller.dispatch(PageAction.Solve(pageB, ConsumptionMode.ALL))
                val completedB = controller.awaitResolution(pageB, ResolutionStatus.COMPLETED)
                completedB.resolution.solutions.size shouldBe 1
                (
                    controller.state.value.workspace
                        .page(pageA)
                        ?.query
                        ?.text
                ) shouldBe "p(X)."

                controller.dispatch(PageAction.Next(pageA, ConsumptionMode.ALL))
                val completedA = controller.awaitResolution(pageA, ResolutionStatus.COMPLETED)
                completedA.resolution.solutions.size shouldBe 2
                completedA.resolution.submittedAt shouldBe submittedAt
                completedA.history.resolutions
                    .single()
                    .submittedAt shouldBe submittedAt
                controller.state.value.workspace.selectedPageId shouldBe pageB
            } finally {
                controller.shutdown()
            }
        }

        test("background-page-result-never-mutates-selected-page").config(coroutineTestScope = true) {
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
                completedA.resolution.solutions.size shouldBe 1
                val foreground =
                    controller.state.value.workspace
                        .page(pageB)!!
                foreground.query.text shouldBe "q(Y)."
                foreground.resolution.status shouldBe ResolutionStatus.IDLE
                foreground.resolution.solutions
                    .isEmpty()
                    .shouldBeTrue()
                controller.state.value.workspace.selectedPageId shouldBe pageB
            } finally {
                controller.shutdown()
            }
        }

        test("stopped-resolution-cannot-publish-a-late-result").config(coroutineTestScope = true) {
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
                stopped.resolution.status shouldBe ResolutionStatus.IDLE
                stopped.resolution.solutions
                    .isEmpty()
                    .shouldBeTrue()

                controller.dispatch(PageAction.Solve(page, ConsumptionMode.ALL))
                val completed = controller.awaitResolution(page, ResolutionStatus.COMPLETED)
                completed.resolution.solutions.size shouldBe 2
            } finally {
                controller.shutdown()
            }
        }

        test("changing-shared-document-invalidates-every-referencing-page").config(coroutineTestScope = true) {
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
                factory.creationRequests.size shouldBe 2

                controller.dispatch(DocumentAction.ChangeText(document, "p(1).\np(2)."))
                val state = controller.state.value.workspace
                state.page(pageA)?.solverSession?.lifecycle shouldBe SolverSessionLifecycle.STALE
                state.page(pageB)?.solverSession?.lifecycle shouldBe SolverSessionLifecycle.STALE
                state.page(pageA)?.resolution?.status shouldBe ResolutionStatus.IDLE
                state.page(pageB)?.resolution?.status shouldBe ResolutionStatus.IDLE
            } finally {
                controller.shutdown()
            }
        }

        test("dirty-close-and-reload-are-explicit-platform-effects").config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(
                    WorkspaceAction.OpenDocumentLoaded(DocumentOrigin("test", "a", "a.pl"), "p(1)."),
                )
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
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                closeEffect.await().shouldBeInstanceOf<GuiEffect.ConfirmCloseDirtyPage>()
                (
                    controller.state.value.workspace
                        .page(page)
                ).shouldNotBeNull()

                val reloadEffect =
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.RequestReload(document))
                reloadEffect.await().shouldBeInstanceOf<GuiEffect.ConfirmReloadDirtyDocument>()

                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.DISCARD))
                (
                    controller.state.value.workspace
                        .page(page)
                ).shouldBeNull()
            } finally {
                controller.shutdown()
            }
        }

        test("save-then-close-does-not-lose-an-edit-made-during-the-write").config(coroutineTestScope = true) {
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
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                closeEffect.await().shouldBeInstanceOf<GuiEffect.ConfirmCloseDirtyPage>()

                val destinationEffect =
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
                destinationEffect.await().shouldBeInstanceOf<GuiEffect.PickSaveDestination>()

                val origin = DocumentOrigin("test", "race", "race.pl")
                val writeEffect =
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                val write = writeEffect.await().shouldBeInstanceOf<GuiEffect.WriteDocument>()
                write.revision shouldBe 1

                controller.dispatch(DocumentAction.ChangeText(document, "p(2)."))
                controller.dispatch(
                    DocumentAction.SaveSucceeded(document, origin, savedRevision = write.revision),
                )

                val state = controller.state.value.workspace
                state.page(page).shouldNotBeNull()
                state.document(document)!!.isDirty.shouldBeTrue()
                state.document(document)?.text shouldBe "p(2)."
            } finally {
                controller.shutdown()
            }
        }

        test(
            "produced-solutions-remain-unread-until-the-solutions-panel-is-acknowledged",
        ).config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("solutions.pl", "p(1)."))
                val pageId = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageId, "p(X)."))
                controller.dispatch(PageAction.Solve(pageId, ConsumptionMode.ALL))
                val completed = controller.awaitResolution(pageId, ResolutionStatus.COMPLETED)
                completed.resolution.hasUnreadChanges.shouldBeTrue()

                controller.dispatch(PageAction.MarkPanelRead(pageId, PanelId.SOLUTIONS))
                (
                    controller.state.value.workspace
                        .page(pageId)!!
                        .resolution.hasUnreadChanges
                ).shouldBeFalse()
            } finally {
                controller.shutdown()
            }
        }

        test("cancelled-non-cooperative-failure-cannot-overwrite-terminal-state").config(coroutineTestScope = true) {
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
                resolution.status shouldBe ResolutionStatus.CANCELLED
                resolution.error.shouldBeNull()
            } finally {
                controller.shutdown()
            }
        }

        test("cancelled-save-before-close-does-not-close-page-after-a-later-save").config(coroutineTestScope = true) {
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
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                closeEffect.await().shouldBeInstanceOf<GuiEffect.ConfirmCloseDirtyPage>()
                val destinationEffect =
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
                destinationEffect.await().shouldBeInstanceOf<GuiEffect.PickSaveDestination>()
                controller.dispatch(DocumentAction.SaveCancelled(document))

                val origin = DocumentOrigin("test", "cancel-save", "cancel-save.pl")
                val writeEffect =
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                val write = writeEffect.await().shouldBeInstanceOf<GuiEffect.WriteDocument>()
                controller.dispatch(DocumentAction.SaveSucceeded(document, origin, write.revision))

                (
                    controller.state.value.workspace
                        .page(page)
                ).shouldNotBeNull()
                (
                    controller.state.value.workspace
                        .document(document)!!
                        .isDirty
                ).shouldBeFalse()
            } finally {
                controller.shutdown()
            }
        }

        test("failed-save-before-close-does-not-close-page-after-a-later-save").config(coroutineTestScope = true) {
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
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.RequestClosePage(page))
                closeEffect.await().shouldBeInstanceOf<GuiEffect.ConfirmCloseDirtyPage>()
                val destinationEffect =
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(WorkspaceAction.ClosePageDecisionProvided(page, CloseDecision.SAVE))
                destinationEffect.await().shouldBeInstanceOf<GuiEffect.PickSaveDestination>()
                controller.dispatch(DocumentAction.SaveFailed(document, "disk full"))

                val origin = DocumentOrigin("test", "failed-save", "failed-save.pl")
                val writeEffect =
                    async(
                        start = CoroutineStart.UNDISPATCHED,
                    ) { withTimeout(5_000) { controller.effects.first() } }
                controller.dispatch(DocumentAction.SaveDestinationSelected(document, origin))
                val write = writeEffect.await().shouldBeInstanceOf<GuiEffect.WriteDocument>()
                controller.dispatch(DocumentAction.SaveSucceeded(document, origin, write.revision))

                (
                    controller.state.value.workspace
                        .page(page)
                ).shouldNotBeNull()
            } finally {
                controller.shutdown()
            }
        }

        test("changing-awaiting-query-records-cancellation-history").config(coroutineTestScope = true) {
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
                state.resolution.status shouldBe ResolutionStatus.IDLE
                state.query.text shouldBe "q(Y)."
                state.history.resolutions.size shouldBe 1
                (
                    state.history.resolutions
                        .single()
                        .terminalStatus
                ) shouldBe ResolutionStatus.CANCELLED
                (
                    state.history.resolutions
                        .single()
                        .query
                ) shouldBe "p(X)."
            } finally {
                controller.shutdown()
            }
        }

        test("stale-save-completion-cannot-regress-persisted-revision").config(coroutineTestScope = true) {
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
                saved.persistedRevision shouldBe 2L
                saved.origin shouldBe second
                saved.isDirty.shouldBeFalse()
            } finally {
                controller.shutdown()
            }
        }

        test(
            "new-page-seeds-syntax-highlighting-with-profile-default-operators-before-any-resolution",
        ).config(coroutineTestScope = true) {
            val dialectOperator = OperatorPresentation("::", 200, "XFX")
            val factory = TestSolverFactory(defaultOperators = listOf(dialectOperator))
            val controller =
                DefaultGuiController(
                    workspaceConfiguration = WorkspaceConfiguration(defaultSolverProfileId = testProfileId),
                    baseProfiles = listOf(factory.profile),
                    parentScope = this,
                )
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1)."))
                val pageId = controller.state.value.workspace.selectedPageId!!
                val page =
                    controller.state.value.workspace
                        .page(pageId)!!
                page.solverSession.inspection.operators shouldBe listOf(dialectOperator)
                page.solverSession.lifecycle shouldBe SolverSessionLifecycle.ABSENT
            } finally {
                controller.shutdown()
            }
        }

        test("scratch-page-has-no-document-and-retains-page-specific-query").config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewScratchPage("scratch", "p(1)."))
                val page = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(page, "p(X)."))
                val state = controller.state.value.workspace
                state.page(page)?.content.shouldBeInstanceOf<PageContent.Scratch>()
                state.documents.isEmpty().shouldBeTrue()
                state.page(page)?.query?.text shouldBe "p(X)."
                state
                    .page(page)
                    ?.title
                    .isNullOrBlank()
                    .shouldBeFalse()
            } finally {
                controller.shutdown()
            }
        }

        test(
            "document-text-changes-update-syntax-diagnostics-without-clobbering-solver-diagnostics",
        ).config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1)."))
                val pageId = controller.state.value.workspace.selectedPageId!!
                val createdPage =
                    controller.state.value.workspace
                        .page(pageId)!!
                val documentId = (createdPage.content as PageContent.DocumentReference).documentId

                controller.dispatch(DocumentAction.ChangeText(documentId, "p(1"))
                val invalid =
                    controller.state.value.workspace
                        .page(pageId)!!
                invalid.semanticTokens.isNotEmpty().shouldBeTrue()
                val syntaxDiagnostic = invalid.diagnostics.values.single()
                syntaxDiagnostic.source shouldBe DiagnosticSources.SYNTAX
                syntaxDiagnostic.severity shouldBe DiagnosticSeverity.ERROR

                controller.dispatch(DocumentAction.ChangeText(documentId, "p(1)."))
                val fixed =
                    controller.state.value.workspace
                        .page(pageId)!!
                (fixed.diagnostics.values.none { it.source == DiagnosticSources.SYNTAX }).shouldBeTrue()
            } finally {
                controller.shutdown()
            }
        }

        test(
            "clear-history-removes-concluded-queries-but-protects-an-ongoing-resolution",
        ).config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl"))
                val pageId = controller.state.value.workspace.selectedPageId!!

                controller.dispatch(PageAction.ChangeQuery(pageId, "q(Y)."))
                controller.dispatch(PageAction.Solve(pageId, ConsumptionMode.ALL))
                controller.awaitResolution(pageId, ResolutionStatus.COMPLETED)
                (
                    controller.state.value.workspace
                        .page(pageId)!!
                        .history.resolutions.size
                ) shouldBe 1

                controller.dispatch(PageAction.ChangeQuery(pageId, "p(X)."))
                controller.dispatch(PageAction.Solve(pageId))
                val awaiting = controller.awaitResolution(pageId, ResolutionStatus.AWAITING_CONTINUATION)
                awaiting.resolution.solutions.size shouldBe 1

                controller.dispatch(PageAction.ClearHistory(pageId))
                val afterClear =
                    controller.state.value.workspace
                        .page(pageId)!!
                afterClear.history.resolutions
                    .isEmpty()
                    .shouldBeTrue()
                afterClear.resolution.status shouldBe ResolutionStatus.AWAITING_CONTINUATION
                afterClear.resolution.solutions.size shouldBe 1

                controller.dispatch(PageAction.Next(pageId, ConsumptionMode.ALL))
                controller.awaitResolution(pageId, ResolutionStatus.COMPLETED)
                (
                    controller.state.value.workspace
                        .page(pageId)!!
                        .history.resolutions.size
                ) shouldBe 1
            } finally {
                controller.shutdown()
            }
        }

        test("delete-resolution/removes-only-the-selected-entry").config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl"))
                val pageId = controller.state.value.workspace.selectedPageId!!
                val first = ResolutionHistoryEntry("p.", emptyList(), ResolutionStatus.COMPLETED, submittedAt = 1L)
                val second = first.copy(submittedAt = 2L)
                controller.dispatch(PageAction.RestoreHistory(pageId, listOf("p."), listOf(first, second)))
                controller.dispatch(PageAction.DeleteResolution(pageId, second))
                controller.state.value.workspace
                    .page(pageId)!!
                    .history.resolutions shouldBe listOf(first)
                controller.dispatch(PageAction.DeleteResolution(pageId, second))
                controller.state.value.workspace
                    .page(pageId)!!
                    .history.resolutions shouldBe listOf(first)
                controller.state.value.workspace
                    .page(pageId)!!
                    .query.history.entries shouldBe listOf("p.")
            } finally {
                controller.shutdown()
            }
        }

        test(
            "restore-history-replaces-query-and-resolution-history-without-touching-live-resolution",
        ).config(coroutineTestScope = true) {
            val (controller, _) = fixture(this)
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl"))
                val pageId = controller.state.value.workspace.selectedPageId!!
                controller.dispatch(PageAction.ChangeQuery(pageId, "live query."))

                val restoredEntry =
                    ResolutionHistoryEntry(
                        query = "p(X).",
                        solutions = listOf(SolutionPresentation.Yes(query = "p(X).", solvedQuery = "p(1).")),
                        terminalStatus = ResolutionStatus.COMPLETED,
                    )
                controller.dispatch(
                    PageAction.RestoreHistory(
                        pageId,
                        queryHistory = listOf("p(X).", "q(Y)."),
                        resolutions = listOf(restoredEntry),
                    ),
                )

                val page =
                    controller.state.value.workspace
                        .page(pageId)!!
                page.query.history.entries shouldBe listOf("p(X).", "q(Y).")
                page.history.resolutions shouldBe listOf(restoredEntry)
                // The live, still-being-edited query text is untouched by restoring history.
                page.query.text shouldBe "live query."
            } finally {
                controller.shutdown()
            }
        }
    })
