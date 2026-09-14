package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.DocumentId

/** What a page actually edits: either a shared, independently-persisted document, or throwaway scratch text
 * local to the page itself. */
sealed interface PageContent {
    /** The page edits/queries the document identified by [documentId] (possibly shared with other pages). */
    data class DocumentReference(
        val documentId: DocumentId,
    ) : PageContent

    /** The page holds its own throwaway text, never saved as a standalone document. */
    data class Scratch(
        val text: String,
        val revision: Long = 0,
    ) : PageContent {
        init {
            require(revision >= 0) { "revision must be non-negative" }
        }
    }
}
