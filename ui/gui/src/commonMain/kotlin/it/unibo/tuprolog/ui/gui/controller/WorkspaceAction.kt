package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin

sealed interface WorkspaceAction : GuiAction {
    data class NewDocumentPage(
        val suggestedName: String? = null,
        val initialText: String = "",
    ) : WorkspaceAction

    data class NewPageForDocument(
        val documentId: DocumentId,
        val suggestedTitle: String? = null,
    ) : WorkspaceAction

    data class NewScratchPage(
        val suggestedTitle: String? = null,
        val initialText: String = "",
    ) : WorkspaceAction

    data class SelectPage(
        val pageId: PageId,
    ) : WorkspaceAction

    data object RequestOpenDocument : WorkspaceAction

    data class OpenDocumentLoaded(
        val origin: DocumentOrigin,
        val text: String,
        /** Whether [text] already diverges from what [origin] holds on disk, e.g. when restoring unsaved edits. */
        val pending: Boolean = false,
    ) : WorkspaceAction

    data class RequestClosePage(
        val pageId: PageId,
    ) : WorkspaceAction

    data class ClosePageDecisionProvided(
        val pageId: PageId,
        val decision: CloseDecision,
    ) : WorkspaceAction
}
