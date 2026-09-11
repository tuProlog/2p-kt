package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin

sealed interface DocumentAction : GuiAction {
    val documentId: DocumentId

    data class ChangeText(
        override val documentId: DocumentId,
        val text: String,
    ) : DocumentAction

    data class Rename(
        override val documentId: DocumentId,
        val displayName: String,
    ) : DocumentAction

    data class RequestSave(
        override val documentId: DocumentId,
        val forceSaveAs: Boolean = false,
    ) : DocumentAction

    data class SaveDestinationSelected(
        override val documentId: DocumentId,
        val origin: DocumentOrigin,
    ) : DocumentAction

    data class SaveCancelled(
        override val documentId: DocumentId,
    ) : DocumentAction

    data class SaveSucceeded(
        override val documentId: DocumentId,
        val origin: DocumentOrigin,
        val savedRevision: Long,
    ) : DocumentAction

    data class SaveFailed(
        override val documentId: DocumentId,
        val message: String,
    ) : DocumentAction

    data class RequestReload(
        override val documentId: DocumentId,
    ) : DocumentAction

    data class ReloadDecisionProvided(
        override val documentId: DocumentId,
        val decision: ReloadDecision,
    ) : DocumentAction

    data class ReloadSucceeded(
        override val documentId: DocumentId,
        val text: String,
    ) : DocumentAction

    data class ReloadFailed(
        override val documentId: DocumentId,
        val message: String,
    ) : DocumentAction
}
