@file:Suppress("InjectDispatcher")

package it.unibo.tuprolog.ui.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.ui.gui.application.buildGuiApplication
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.persistence.PersistedWorkspace
import it.unibo.tuprolog.ui.gui.persistence.capturePersistedWorkspace
import it.unibo.tuprolog.ui.gui.persistence.restoreWorkspace
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// Dispatchers.Default drives a throwaway CoroutineScope local to each test case; there is nothing downstream
// to inject a dispatcher into here, unlike the production code this test exercises.
class WorkspacePersistenceSpec :
    FunSpec({
        fun testApplication(profileId: String) =
            buildGuiApplication(CoroutineScope(SupervisorJob() + Dispatchers.Default)) {
                solverProfile(
                    solverFactoryProfile(
                        PrologSolverFactory,
                        SolverProfileId(profileId),
                        profileId,
                        runtimeLibraries = listOf(IOLib),
                    ),
                    makeDefault = true,
                )
            }

        fun capturePersistedWorkspaceStub() = PersistedWorkspace(fontSize = 14, documents = emptyList())

        test(
            "a captured workspace round-trips through localStorage and restores query, history and font size",
        ).config(coroutineTestScope = true) {
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
                snapshot.fontSize shouldBe 18
                WebWorkspacePersistence.save(snapshot)
                val loaded = WebWorkspacePersistence.load()
                loaded shouldBe snapshot

                val application2 = testApplication("test2")
                application2.start()
                try {
                    restoreWorkspace(loaded!!, application2.controller)
                    val restoredPage =
                        application2.controller.state.value.workspace.pages
                            .single()
                    restoredPage.content.shouldBeInstanceOf<PageContent.DocumentReference>()
                    restoredPage.query.text shouldBe "p(X)."
                    restoredPage.query.history.entries shouldBe listOf("p(1).")
                    restoredPage.history.resolutions.size shouldBe 1
                } finally {
                    application2.close()
                }
            } finally {
                application.close()
                WebWorkspacePersistence.delete()
            }
        }

        test("delete removes the persisted entry and load then reports nothing saved") {
            WebWorkspacePersistence.save(capturePersistedWorkspaceStub())
            WebWorkspacePersistence.delete().shouldBeTrue()
            WebWorkspacePersistence.load().shouldBeNull()
            WebWorkspacePersistence.delete() shouldBe false
        }
    })
