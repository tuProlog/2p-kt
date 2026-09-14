package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin

/** Actions about one document's lifecycle: editing, saving, and reloading it from its origin. */
sealed interface DocumentAction : GuiAction {
    /** The document this action applies to. */
    val documentId: DocumentId

    /** The document's in-memory text was edited. */
    data class ChangeText(
        override val documentId: DocumentId,
        val text: String,
    ) : DocumentAction

    /** The document's display name was changed. */
    data class Rename(
        override val documentId: DocumentId,
        val displayName: String,
    ) : DocumentAction

    /** The user asked to save; [forceSaveAs] requests a destination picker even if the document already has one. */
    data class RequestSave(
        override val documentId: DocumentId,
        val forceSaveAs: Boolean = false,
    ) : DocumentAction

    /** The user picked (or confirmed) where to save a document that didn't already have an [origin]. */
    data class SaveDestinationSelected(
        override val documentId: DocumentId,
        val origin: DocumentOrigin,
    ) : DocumentAction

    /** The user backed out of a save-destination picker; the document stays dirty. */
    data class SaveCancelled(
        override val documentId: DocumentId,
    ) : DocumentAction

    /** The write to [origin] completed; the document is no longer dirty as of [savedRevision]. */
    data class SaveSucceeded(
        override val documentId: DocumentId,
        val origin: DocumentOrigin,
        val savedRevision: Long,
    ) : DocumentAction

    /** The write failed; the document stays dirty and [message] is shown to the user. */
    data class SaveFailed(
        override val documentId: DocumentId,
        val message: String,
    ) : DocumentAction

    /** The user asked to reload the document from its origin, discarding in-memory changes if confirmed. */
    data class RequestReload(
        override val documentId: DocumentId,
    ) : DocumentAction

    /** The user resolved a reload-with-dirty-changes prompt. */
    data class ReloadDecisionProvided(
        override val documentId: DocumentId,
        val decision: ReloadDecision,
    ) : DocumentAction

    /** The document was re-read from its origin; [text] replaces its in-memory content. */
    data class ReloadSucceeded(
        override val documentId: DocumentId,
        val text: String,
    ) : DocumentAction

    /** Re-reading the document from its origin failed; in-memory content is unchanged. */
    data class ReloadFailed(
        override val documentId: DocumentId,
        val message: String,
    ) : DocumentAction
}
