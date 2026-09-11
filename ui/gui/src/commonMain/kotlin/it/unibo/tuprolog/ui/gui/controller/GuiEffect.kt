package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.EffectId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin

sealed interface GuiEffect {
    val id: EffectId

    data class PickOpenDocument(
        override val id: EffectId,
        val acceptedExtensions: Set<String> = setOf("pl", "2p", "txt"),
    ) : GuiEffect

    data class PickSaveDestination(
        override val id: EffectId,
        val documentId: DocumentId,
        val suggestedName: String,
    ) : GuiEffect

    data class WriteDocument(
        override val id: EffectId,
        val documentId: DocumentId,
        val origin: DocumentOrigin,
        val text: String,
        val revision: Long,
    ) : GuiEffect

    data class ReadDocument(
        override val id: EffectId,
        val documentId: DocumentId,
        val origin: DocumentOrigin,
    ) : GuiEffect

    data class ConfirmCloseDirtyPage(
        override val id: EffectId,
        val pageId: PageId,
        val documentId: DocumentId,
        val displayName: String,
    ) : GuiEffect

    data class ConfirmReloadDirtyDocument(
        override val id: EffectId,
        val documentId: DocumentId,
        val displayName: String,
    ) : GuiEffect

    data class ConfirmExitWithDirtyDocuments(
        override val id: EffectId,
        val dirtyDocuments: List<DocumentId>,
    ) : GuiEffect

    data class CopyTextToClipboard(
        override val id: EffectId,
        val text: String,
    ) : GuiEffect

    data class ExitApplication(
        override val id: EffectId,
    ) : GuiEffect
}
