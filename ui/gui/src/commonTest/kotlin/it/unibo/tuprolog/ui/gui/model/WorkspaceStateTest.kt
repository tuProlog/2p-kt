package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WorkspaceStateTest {
    private val configuration = WorkspaceConfiguration(defaultSolverProfileId = SolverProfileId("test"))

    @Test
    fun selectedPageMustReferenceAnExistingPage() {
        assertFailsWith<IllegalArgumentException> {
            WorkspaceState(selectedPageId = PageId("missing"), configuration = configuration)
        }
    }

    @Test
    fun aDocumentReferencePageMustReferenceAnExistingDocument() {
        val page = PageState(PageId("p"), "p", PageContent.DocumentReference(DocumentId("missing")))
        assertFailsWith<IllegalArgumentException> {
            WorkspaceState(pages = listOf(page), configuration = configuration)
        }
    }

    @Test
    fun updatePageFailsForAnUnknownPage() {
        val workspace = WorkspaceState(configuration = configuration)
        assertFailsWith<IllegalArgumentException> {
            workspace.updatePage(PageId("missing")) { it }
        }
    }

    @Test
    fun updateDocumentFailsForAnUnknownDocument() {
        val workspace = WorkspaceState(configuration = configuration)
        assertFailsWith<IllegalStateException> {
            workspace.updateDocument(DocumentId("missing")) { it }
        }
    }

    @Test
    fun pageAndDocumentLookUpByIdSucceedWhenPresent() {
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
}
