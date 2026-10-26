package it.unibo.tuprolog.ui.gui.model

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WorkspaceStateSpec :
    FunSpec({
        val configuration = WorkspaceConfiguration(defaultSolverProfileId = SolverProfileId("test"))

        test("selected-page-must-reference-an-existing-page") {
            assertFailsWith<IllegalArgumentException> {
                WorkspaceState(selectedPageId = PageId("missing"), configuration = configuration)
            }
        }

        test("a-document-reference-page-must-reference-an-existing-document") {
            val page = PageState(PageId("p"), "p", PageContent.DocumentReference(DocumentId("missing")))
            assertFailsWith<IllegalArgumentException> {
                WorkspaceState(pages = listOf(page), configuration = configuration)
            }
        }

        test("update-page-fails-for-an-unknown-page") {
            val workspace = WorkspaceState(configuration = configuration)
            assertFailsWith<IllegalArgumentException> {
                workspace.updatePage(PageId("missing")) { it }
            }
        }

        test("update-document-fails-for-an-unknown-document") {
            val workspace = WorkspaceState(configuration = configuration)
            assertFailsWith<IllegalStateException> {
                workspace.updateDocument(DocumentId("missing")) { it }
            }
        }

        test("page-and-document-look-up-by-id-succeed-when-present") {
            val document = DocumentState(id = DocumentId("d"), displayName = "d")
            val page = PageState(PageId("p"), "p", PageContent.DocumentReference(document.id))
            val workspace =
                WorkspaceState(
                    documents = mapOf(document.id to document),
                    pages = listOf(page),
                    configuration = configuration,
                )
            assertEquals(page, workspace.page(PageId("p")))
            assertEquals(document, workspace.document(DocumentId("d")))
            assertEquals(null, workspace.page(PageId("missing")))
        }
    })
