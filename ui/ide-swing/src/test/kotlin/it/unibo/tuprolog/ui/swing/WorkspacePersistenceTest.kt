package it.unibo.tuprolog.ui.swing

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
import it.unibo.tuprolog.ui.gui.persistence.PersistedWorkspace
import it.unibo.tuprolog.ui.gui.persistence.restoreWorkspace
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// Dispatchers.Default drives a throwaway CoroutineScope local to each test case; there is nothing downstream
// to inject a dispatcher into here, unlike the production code this test exercises.
@Suppress("InjectDispatcher")
class WorkspacePersistenceTest {
    private fun testApplication(profileId: String) =
        buildGuiApplication(CoroutineScope(SupervisorJob() + Dispatchers.Default)) {
            solverProfile(
                solverFactoryProfile(Solver.prolog, SolverProfileId(profileId), profileId),
                makeDefault = true,
            )
        }

    @Test
    fun `a saved workspace round-trips through disk and restores as pages`() =
        runBlocking {
            val application = testApplication("test")
            application.start()
            val controller = application.controller
            try {
                controller.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1)."))
                controller.dispatch(WorkspaceAction.NewScratchPage("scratch", "q(2)."))

                val snapshot = capturePersistedWorkspace(controller.state.value, fontSize = 18, windowBounds = null)
                assertEquals(2, snapshot.documents.size)
                assertEquals(18, snapshot.fontSize)
                // The freshly-created document was never saved to disk, so it must be reported as dirty.
                assertTrue(snapshot.documents.any { it.dirty })

                val persistence = WorkspacePersistence("test-app", createTempDirectory().toFile())
                persistence.save(snapshot)
                val loaded = persistence.load()
                assertNotNull(loaded)
                assertEquals(snapshot, loaded)

                val application2 = testApplication("test2")
                application2.start()
                try {
                    restoreWorkspace(loaded, application2.controller)
                    val pages = application2.controller.state.value.workspace.pages
                    assertEquals(2, pages.size)
                    assertTrue(
                        pages.any {
                            it.content is PageContent.Scratch &&
                                (it.content as PageContent.Scratch).text == "q(2)."
                        },
                    )
                    val restoredDocPage = pages.first { it.content is PageContent.DocumentReference }
                    val documentId = (restoredDocPage.content as PageContent.DocumentReference).documentId
                    val restoredDocument =
                        application2.controller.state.value.workspace
                            .document(documentId)
                    assertEquals("p(1).", restoredDocument?.text)
                    assertTrue(restoredDocument?.isDirty == true)
                } finally {
                    application2.close()
                }
            } finally {
                application.close()
            }
        }

    @Test
    fun `restoring a workspace also restores each page's query, query history, solutions history, and zoom`() =
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
                    assertEquals("p(X).", restoredPage.query.text)
                    assertEquals(listOf("p(1)."), restoredPage.query.history.entries)
                    assertEquals(listOf(restoredEntry), restoredPage.history.resolutions)
                } finally {
                    application2.close()
                }
            } finally {
                application.close()
            }
        }

    private fun captureSnapshotAndAssert(
        state: GuiState,
        pageId: PageId,
    ): PersistedWorkspace {
        val snapshot =
            capturePersistedWorkspace(state, fontSize = 14, windowBounds = null, pageFontSizes = mapOf(pageId to 22))
        assertEquals("p(X).", snapshot.documents.single().query)
        assertEquals(listOf("p(1)."), snapshot.documents.single().queryHistory)
        assertEquals(22, snapshot.documents.single().fontSize)
        assertEquals(
            1,
            snapshot.documents
                .single()
                .resolutions.size,
        )
        return snapshot
    }

    @Test
    fun `delete removes the persisted file and load then reports nothing saved`() {
        val persistence = WorkspacePersistence("delete-test-app", createTempDirectory().toFile())
        persistence.save(PersistedWorkspace(fontSize = 18))
        assertNotNull(persistence.load())

        assertTrue(persistence.delete())

        assertEquals(null, persistence.load())
        // Deleting again (nothing left to delete) is reported, not thrown, so callers don't need to guard it.
        assertEquals(false, persistence.delete())
    }
}
