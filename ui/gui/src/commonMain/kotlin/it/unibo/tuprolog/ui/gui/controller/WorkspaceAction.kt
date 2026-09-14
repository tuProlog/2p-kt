package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin

/** Actions about the workspace as a whole: creating/opening/closing/selecting pages, rather than editing an
 * existing one's content. */
sealed interface WorkspaceAction : GuiAction {
    /** Creates a new page backed by a brand-new, not-yet-saved document. */
    data class NewDocumentPage(
        val suggestedName: String? = null,
        val initialText: String = "",
    ) : WorkspaceAction

    /** Creates another page for an already-open document (so it can be edited/queried independently in parallel). */
    data class NewPageForDocument(
        val documentId: DocumentId,
        val suggestedTitle: String? = null,
    ) : WorkspaceAction

    /** Creates a new page backed by a page-local scratch buffer, never persisted as its own document. */
    data class NewScratchPage(
        val suggestedTitle: String? = null,
        val initialText: String = "",
    ) : WorkspaceAction

    /** Makes [pageId] the workspace's selected page. */
    data class SelectPage(
        val pageId: PageId,
    ) : WorkspaceAction

    /** Asks the hosting platform to show an "open file" picker (see `GuiEffect.PickOpenDocument`). */
    data object RequestOpenDocument : WorkspaceAction

    /** A document was read from an external source and should become a new page. */
    data class OpenDocumentLoaded(
        val origin: DocumentOrigin,
        val text: String,
        /** Whether [text] already diverges from what [origin] holds on disk, e.g. when restoring unsaved edits. */
        val pending: Boolean = false,
    ) : WorkspaceAction

    /** The user asked to close a page; if its document is dirty, a confirmation effect is requested first. */
    data class RequestClosePage(
        val pageId: PageId,
    ) : WorkspaceAction

    /** The user resolved a close-dirty-page prompt. */
    data class ClosePageDecisionProvided(
        val pageId: PageId,
        val decision: CloseDecision,
    ) : WorkspaceAction
}
