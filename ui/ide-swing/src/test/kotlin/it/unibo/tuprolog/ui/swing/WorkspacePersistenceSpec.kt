@file:Suppress("InjectDispatcher")

package it.unibo.tuprolog.ui.swing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.persistence.PersistedResolution
import it.unibo.tuprolog.ui.gui.persistence.PersistedWorkspace
import it.unibo.tuprolog.ui.gui.persistence.restoreWorkspace
import it.unibo.tuprolog.ui.gui.persistence.toPersisted
import it.unibo.tuprolog.ui.gui.persistence.toResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import it.unibo.tuprolog.ui.swing.app.WorkspacePersistence
import it.unibo.tuprolog.ui.swing.app.capturePersistedWorkspace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.io.path.createTempDirectory
import kotlin.time.Duration.Companion.milliseconds

// Dispatchers.Default drives a throwaway CoroutineScope local to each test case; there is nothing downstream
// to inject a dispatcher into here, unlike the production code this test exercises.
class WorkspacePersistenceSpec :
    FunSpec({
        fun testApplication(profileId: String) =
            buildGuiApplication(CoroutineScope(SupervisorJob() + Dispatchers.Default)) {
                solverProfile(
                    solverFactoryProfile(Solver.prolog, SolverProfileId(profileId), profileId),
                    makeDefault = true,
                )
            }

        fun captureSnapshotAndAssert(
            state: GuiState,
            pageId: PageId,
        ): PersistedWorkspace {
            val snapshot =
                capturePersistedWorkspace(
                    state,
                    fontSize = 14,
                    windowBounds = null,
                    pageFontSizes = mapOf(pageId to 22),
                )
            snapshot.documents.single().query shouldBe "p(X)."
            snapshot.documents.single().queryHistory shouldBe listOf("p(1).")
            snapshot.documents.single().fontSize shouldBe 22
            (
                snapshot.documents
                    .single()
                    .resolutions.size
            ) shouldBe 1
            return snapshot
        }

        test("a saved workspace round-trips through disk and restores as pages") {
            runBlocking {
                val application = testApplication("test")
                application.start()
                val controller = application.controller
                try {
                    controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1)."))
                    controller.dispatch(WorkspaceAction.NewScratchPage("scratch", "q(2)."))

                    val snapshot =
                        capturePersistedWorkspace(controller.state.value, fontSize = 18, windowBounds = null)
                    snapshot.documents.size shouldBe 2
                    snapshot.fontSize shouldBe 18
                    // The freshly-created document was never saved to disk, so it must be reported as dirty.
                    (snapshot.documents.any { it.dirty }).shouldBeTrue()

                    val persistence = WorkspacePersistence("test-app", createTempDirectory().toFile())
                    persistence.save(snapshot)
                    val loaded = persistence.load()
                    loaded.shouldNotBeNull()
                    loaded shouldBe snapshot

                    val application2 = testApplication("test2")
                    application2.start()
                    try {
                        restoreWorkspace(loaded, application2.controller)
                        val pages = application2.controller.state.value.workspace.pages
                        pages.size shouldBe 2
                        (
                            pages.any {
                                it.content is PageContent.Scratch &&
                                    (it.content as PageContent.Scratch).text == "q(2)."
                            }
                        ).shouldBeTrue()
                        val restoredDocPage = pages.first { it.content is PageContent.DocumentReference }
                        val documentId = (restoredDocPage.content as PageContent.DocumentReference).documentId
                        val restoredDocument =
                            application2.controller.state.value.workspace
                                .document(documentId)
                        restoredDocument?.text shouldBe "p(1)."
                        restoredDocument?.isDirty shouldBe true
                    } finally {
                        application2.close()
                    }
                } finally {
                    application.close()
                }
            }
        }

        test("restoring a workspace also restores each page's query, query history, solutions history, and zoom") {
            runBlocking {
                val application = testApplication("test")
                application.start()
                val controller = application.controller
                try {
                    controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1)."))
                    val pageId = controller.state.value.workspace.selectedPageId!!
                    controller.dispatch(PageAction.ChangeQuery(pageId, "p(X)."))
                    val restoredEntry =
                        ResolutionHistoryEntry(
                            query = "p(1).",
                            solutions = listOf(SolutionPresentation.Yes(query = "p(1).", solvedQuery = "p(1).")),
                            terminalStatus = ResolutionStatus.COMPLETED,
                        )
                    controller.dispatch(
                        PageAction.RestoreHistory(
                            pageId,
                            queryHistory = listOf("p(1)."),
                            resolutions = listOf(restoredEntry),
                        ),
                    )

                    val snapshot = captureSnapshotAndAssert(controller.state.value, pageId)

                    val application2 = testApplication("test2")
                    application2.start()
                    try {
                        restoreWorkspace(snapshot, application2.controller)
                        val restoredPage =
                            application2.controller.state.value.workspace.pages
                                .single()
                        restoredPage.query.text shouldBe "p(X)."
                        restoredPage.query.history.entries shouldBe listOf("p(1).")
                        restoredPage.history.resolutions shouldBe listOf(restoredEntry)
                    } finally {
                        application2.close()
                    }
                } finally {
                    application.close()
                }
            }
        }

        test("delete removes the persisted file and load then reports nothing saved") {
            val persistence = WorkspacePersistence("delete-test-app", createTempDirectory().toFile())
            persistence.save(PersistedWorkspace(fontSize = 18))
            persistence.load().shouldNotBeNull()

            persistence.delete().shouldBeTrue()

            persistence.load() shouldBe null
            // Deleting again (nothing left to delete) is reported, not thrown, so callers don't need to guard it.
            persistence.delete() shouldBe false
        }

        test("solution-times-survive-the-json-round-trip") {
            val entry =
                ResolutionHistoryEntry(
                    query = "p(X).",
                    solutions =
                        listOf(
                            SolutionPresentation.Yes(query = "p(X).", solvedQuery = "p(1).", time = 3.milliseconds),
                            SolutionPresentation.No(query = "p(X).", time = 1.milliseconds),
                        ),
                    terminalStatus = ResolutionStatus.COMPLETED,
                )
            val json = Json.encodeToString(PersistedResolution.serializer(), entry.toPersisted())

            Json.decodeFromString(PersistedResolution.serializer(), json).toResolutionHistoryEntry() shouldBe entry
        }
    })
