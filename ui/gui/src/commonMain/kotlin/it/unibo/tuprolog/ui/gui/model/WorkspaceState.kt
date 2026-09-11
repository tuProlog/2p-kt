package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId

data class WorkspaceState(
    val documents: Map<DocumentId, DocumentState> = emptyMap(),
    val pages: List<PageState> = emptyList(),
    val selectedPageId: PageId? = null,
    val configuration: WorkspaceConfiguration,
) {
    init {
        require(selectedPageId == null || pages.any { it.id == selectedPageId }) {
            "selectedPageId must reference an existing page"
        }
        for (page in pages) {
            val content = page.content
            if (content is PageContent.DocumentReference) {
                require(content.documentId in documents) {
                    "Page ${page.id} references missing document ${content.documentId}"
                }
            }
        }
    }

    fun page(id: PageId): PageState? = pages.firstOrNull { it.id == id }

    fun document(id: DocumentId): DocumentState? = documents[id]

    fun updatePage(
        id: PageId,
        transform: (PageState) -> PageState,
    ): WorkspaceState {
        var found = false
        val updated =
            pages.map {
                if (it.id == id) {
                    found = true
                    transform(it)
                } else {
                    it
                }
            }
        require(found) { "Unknown page: $id" }
        return copy(pages = updated)
    }

    fun updateDocument(
        id: DocumentId,
        transform: (DocumentState) -> DocumentState,
    ): WorkspaceState {
        val existing = documents[id] ?: error("Unknown document: $id")
        return copy(documents = documents + (id to transform(existing)))
    }
}
