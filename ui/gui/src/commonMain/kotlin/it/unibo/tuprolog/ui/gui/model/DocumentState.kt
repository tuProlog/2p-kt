package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.DocumentId

data class DocumentState(
    val id: DocumentId,
    val displayName: String,
    val text: String = "",
    val origin: DocumentOrigin? = null,
    val revision: Long = 0,
    val persistedRevision: Long = 0,
) {
    init {
        require(revision >= 0) { "revision must be non-negative" }
        require(persistedRevision >= 0) { "persistedRevision must be non-negative" }
        require(persistedRevision <= revision) { "persistedRevision cannot exceed revision" }
    }

    val isDirty: Boolean get() = revision != persistedRevision
}
