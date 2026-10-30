package it.unibo.tuprolog.ui.gui.persistence

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.controller.ConsumptionMode
import it.unibo.tuprolog.ui.gui.controller.DefaultGuiController
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.TestSolverFactory
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.controller.awaitResolution
import it.unibo.tuprolog.ui.gui.controller.testProfileId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import kotlinx.coroutines.CoroutineScope

private fun controller(scope: CoroutineScope) =
    DefaultGuiController(
        workspaceConfiguration = WorkspaceConfiguration(defaultSolverProfileId = testProfileId),
        baseProfiles = listOf(TestSolverFactory().profile),
        parentScope = scope,
    )

class WorkspacePersistenceSpec :
    FunSpec({
        test("capture-keeps-every-kind-of-page-and-the-frontend-settings").config(coroutineTestScope = true) {
            val source = controller(this)
            try {
                source.dispatch(WorkspaceAction.NewDocumentPage("a.pl", "p(1).\np(2)."))
                val document = source.state.value.workspace.selectedPageId!!
                source.dispatch(PageAction.ChangeQuery(document, "p(X)."))
                source.dispatch(PageAction.Solve(document, ConsumptionMode.ALL))
                source.awaitResolution(document, ResolutionStatus.COMPLETED)
                source.dispatch(WorkspaceAction.NewScratchPage("notes", "s."))
                source.dispatch(
                    WorkspaceAction.OpenDocumentLoaded(
                        DocumentOrigin("file", "/b.pl", "b.pl"),
                        "b(1).",
                        pending = true,
                    ),
                )
                source.dispatch(WorkspaceAction.SelectPage(document))

                val persisted =
                    capturePersistedWorkspace(
                        source.state.value,
                        fontSize = 14,
                        pageFontSizes = mapOf(document to 20),
                        windowWidth = 800,
                        windowHeight = 600,
                        windowX = 10,
                        windowY = 20,
                        lookAndFeel = "Nimbus",
                    )

                persisted.selectedIndex shouldBe 0
                persisted.fontSize shouldBe 14
                listOf(persisted.windowWidth, persisted.windowHeight, persisted.windowX, persisted.windowY) shouldBe
                    listOf(800, 600, 10, 20)
                persisted.lookAndFeel shouldBe "Nimbus"
                val (a, scratch, b) = persisted.documents
                listOf(a.displayName, a.text, a.query, a.fontSize) shouldBe listOf("a.pl", "p(1).\np(2).", "p(X).", 20)
                a.queryHistory shouldBe listOf("p(X).")
                a.resolutions.size shouldBe 1
                listOf(scratch.isScratch, scratch.displayName, scratch.text, scratch.fontSize) shouldBe
                    listOf(true, "notes", "s.", null)
                listOf(b.originProviderId, b.originReference, b.text, b.dirty) shouldBe
                    listOf("file", "/b.pl", "b(1).", true)
            } finally {
                source.shutdown()
            }
        }

        test("restore-then-capture-gives-back-the-same-workspace").config(coroutineTestScope = true) {
            val persisted =
                PersistedWorkspace(
                    selectedIndex = 1,
                    documents =
                        listOf(
                            // never saved, so its text is unsaved by definition
                            PersistedDocument(
                                "a.pl",
                                "p(1).",
                                dirty = true,
                                query = "p(X).",
                                queryHistory = listOf("p(1).", "p(X)."),
                            ),
                            PersistedDocument("notes", "s.", isScratch = true),
                            PersistedDocument(
                                "b.pl",
                                "b(1).",
                                originProviderId = "file",
                                originReference = "/b.pl",
                                dirty = true,
                            ),
                        ),
                )
            val target = controller(this)
            try {
                restoreWorkspace(persisted, target)
                capturePersistedWorkspace(target.state.value, fontSize = persisted.fontSize) shouldBe persisted
            } finally {
                target.shutdown()
            }
        }

        test("restore-of-an-empty-workspace-opens-nothing").config(coroutineTestScope = true) {
            val target = controller(this)
            try {
                val pagesBefore = target.state.value.workspace.pages
                restoreWorkspace(PersistedWorkspace(), target)
                target.state.value.workspace.pages shouldBe pagesBefore
            } finally {
                target.shutdown()
            }
        }
    })
