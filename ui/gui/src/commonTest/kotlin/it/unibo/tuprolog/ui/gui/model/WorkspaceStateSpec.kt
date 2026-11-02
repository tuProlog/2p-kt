package it.unibo.tuprolog.ui.gui.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId

class WorkspaceStateSpec :
    FunSpec({
        val configuration = WorkspaceConfiguration(defaultSolverProfileId = SolverProfileId("test"))

        test("selected-page-must-reference-an-existing-page") {
            shouldThrow<IllegalArgumentException> {
                WorkspaceState(selectedPageId = PageId("missing"), configuration = configuration)
            }
        }

        test("a-document-reference-page-must-reference-an-existing-document") {
            val page = PageState(PageId("p"), "p", PageContent.DocumentReference(DocumentId("missing")))
            shouldThrow<IllegalArgumentException> {
                WorkspaceState(pages = listOf(page), configuration = configuration)
            }
        }

        test("update-page-fails-for-an-unknown-page") {
            val workspace = WorkspaceState(configuration = configuration)
            shouldThrow<IllegalArgumentException> {
                workspace.updatePage(PageId("missing")) { it }
            }
        }

        test("update-document-fails-for-an-unknown-document") {
            val workspace = WorkspaceState(configuration = configuration)
            shouldThrow<IllegalStateException> {
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
            workspace.page(PageId("p")) shouldBe page
            workspace.document(DocumentId("d")) shouldBe document
            workspace.page(PageId("missing")) shouldBe null
        }
    })
