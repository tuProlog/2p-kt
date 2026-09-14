package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.EffectId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin

/** A one-shot request the controller can't fulfil itself and hands off to the hosting platform (file dialogs,
 * actual I/O, confirmation prompts, clipboard, process exit) - delivered via [GuiModel.effects]. */
sealed interface GuiEffect {
    /** Uniquely identifies this effect instance. */
    val id: EffectId

    /** Show a native "open file" picker restricted to [acceptedExtensions]. */
    data class PickOpenDocument(
        override val id: EffectId,
        val acceptedExtensions: Set<String> = setOf("pl", "2p", "txt"),
    ) : GuiEffect

    /** Show a native "save as" picker for a document with no origin yet, defaulting to [suggestedName]. */
    data class PickSaveDestination(
        override val id: EffectId,
        val documentId: DocumentId,
        val suggestedName: String,
    ) : GuiEffect

    /** Actually persist [text] to [origin]; the platform reports back success/failure via a `DocumentAction`. */
    data class WriteDocument(
        override val id: EffectId,
        val documentId: DocumentId,
        val origin: DocumentOrigin,
        val text: String,
        val revision: Long,
    ) : GuiEffect

    /** Actually read [origin]'s current content, e.g. to reload a document. */
    data class ReadDocument(
        override val id: EffectId,
        val documentId: DocumentId,
        val origin: DocumentOrigin,
    ) : GuiEffect

    /** Ask the user how to handle closing a dirty page (save/discard/cancel). */
    data class ConfirmCloseDirtyPage(
        override val id: EffectId,
        val pageId: PageId,
        val documentId: DocumentId,
        val displayName: String,
    ) : GuiEffect

    /** Ask the user whether to discard unsaved changes before reloading a document. */
    data class ConfirmReloadDirtyDocument(
        override val id: EffectId,
        val documentId: DocumentId,
        val displayName: String,
    ) : GuiEffect

    /** Ask the user to confirm exiting while [dirtyDocuments] still have unsaved changes. */
    data class ConfirmExitWithDirtyDocuments(
        override val id: EffectId,
        val dirtyDocuments: List<DocumentId>,
    ) : GuiEffect

    /** Put [text] on the system clipboard. */
    data class CopyTextToClipboard(
        override val id: EffectId,
        val text: String,
    ) : GuiEffect

    /** Actually terminate the application/close its window. */
    data class ExitApplication(
        override val id: EffectId,
    ) : GuiEffect
}
