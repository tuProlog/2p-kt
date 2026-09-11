package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.DocumentId

sealed interface PageContent {
    data class DocumentReference(
        val documentId: DocumentId,
    ) : PageContent

    data class Scratch(
        val text: String,
        val revision: Long = 0,
    ) : PageContent {
        init {
            require(revision >= 0) { "revision must be non-negative" }
        }
    }
}
