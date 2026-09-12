package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Dispatchers.Default drives a throwaway CoroutineScope local to each test case; there is nothing downstream
// to inject a dispatcher into here, unlike the production code this test exercises.
@Suppress("InjectDispatcher")
class WorkspacePersistenceTest {
    private fun testApplication(profileId: String) =
        buildGuiApplication(CoroutineScope(SupervisorJob() + Dispatchers.Default)) {
            solverProfile(
                solverFactoryProfile(
                    ClassicSolverFactory,
                    SolverProfileId(profileId),
                    profileId,
                    runtimeLibraries = listOf(IOLib),
                ),
                makeDefault = true,
            )
        }

    @Test
    fun `a captured workspace round-trips through localStorage and restores query, history and font size`() =
        runTest {
            WebWorkspacePersistence.delete()
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

                val snapshot = capturePersistedWorkspace(controller.state.value, fontSize = 18)
                assertEquals(18, snapshot.fontSize)
                WebWorkspacePersistence.save(snapshot)
                val loaded = WebWorkspacePersistence.load()
                assertEquals(snapshot, loaded)

                val application2 = testApplication("test2")
                application2.start()
                try {
                    restoreWorkspace(loaded!!, application2.controller)
                    val restoredPage =
                        application2.controller.state.value.workspace.pages
                            .single()
                    assertTrue(restoredPage.content is PageContent.DocumentReference)
                    assertEquals("p(X).", restoredPage.query.text)
                    assertEquals(listOf("p(1)."), restoredPage.query.history.entries)
                    assertEquals(1, restoredPage.history.resolutions.size)
                } finally {
                    application2.close()
                }
            } finally {
                application.close()
                WebWorkspacePersistence.delete()
            }
        }

    @Test
    fun `delete removes the persisted entry and load then reports nothing saved`() {
        WebWorkspacePersistence.save(capturePersistedWorkspaceStub())
        assertTrue(WebWorkspacePersistence.delete())
        assertNull(WebWorkspacePersistence.load())
        assertEquals(false, WebWorkspacePersistence.delete())
    }

    private fun capturePersistedWorkspaceStub() = PersistedWorkspace(fontSize = 14, documents = emptyList())
}
