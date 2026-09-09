package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.solve.solverFactoryProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WorkspacePersistenceTest {
    @Test
    fun `a saved workspace round-trips through disk and restores as pages`() =
        runBlocking {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            val application =
                buildGuiApplication(scope) {
                    solverProfile(
                        solverFactoryProfile(Solver.prolog, SolverProfileId("test"), "Test"),
                        makeDefault = true,
                    )
                }
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

                val scope2 = CoroutineScope(SupervisorJob() + Dispatchers.Default)
                val application2 =
                    buildGuiApplication(scope2) {
                        solverProfile(
                            solverFactoryProfile(Solver.prolog, SolverProfileId("test2"), "Test2"),
                            makeDefault = true,
                        )
                    }
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
}
