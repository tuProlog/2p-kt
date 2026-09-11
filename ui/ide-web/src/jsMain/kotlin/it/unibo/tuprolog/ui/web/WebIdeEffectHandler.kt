package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.controller.ApplicationAction
import it.unibo.tuprolog.ui.gui.controller.CloseDecision
import it.unibo.tuprolog.ui.gui.controller.DocumentAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.GuiEffect
import it.unibo.tuprolog.ui.gui.controller.ReloadDecision
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLTextAreaElement

/** Executes platform effects against `localStorage` and native browser dialogs. */
internal class WebIdeEffectHandler(
    private val controller: GuiController,
    private val scope: CoroutineScope,
) {
    fun handle(effect: GuiEffect) {
        when (effect) {
            is GuiEffect.PickOpenDocument -> pickOpenDocument()
            is GuiEffect.PickSaveDestination -> pickSaveDestination(effect)
            is GuiEffect.WriteDocument -> writeDocument(effect)
            is GuiEffect.ReadDocument -> readDocument(effect)
            is GuiEffect.ConfirmCloseDirtyPage -> confirmClose(effect)
            is GuiEffect.ConfirmReloadDirtyDocument -> confirmReload(effect)
            is GuiEffect.ConfirmExitWithDirtyDocuments -> confirmExit(effect)
            is GuiEffect.CopyTextToClipboard -> copyToClipboard(effect.text)
            is GuiEffect.ExitApplication -> Unit
        }
    }

    private fun pickOpenDocument() {
        showDocumentPicker(
            names = LocalStorageDocumentStore.list(),
            onSelected = { name ->
                val text = LocalStorageDocumentStore.read(name).orEmpty()
                dispatch(WorkspaceAction.OpenDocumentLoaded(name.toOrigin(), text))
            },
            onCancel = {},
        )
    }

    private fun pickSaveDestination(effect: GuiEffect.PickSaveDestination) {
        val name = window.prompt("Save as (name in browser storage):", effect.suggestedName)
        if (name.isNullOrBlank()) {
            dispatch(DocumentAction.SaveCancelled(effect.documentId))
        } else {
            dispatch(DocumentAction.SaveDestinationSelected(effect.documentId, name.toOrigin()))
        }
    }

    private fun writeDocument(effect: GuiEffect.WriteDocument) {
        val name = effect.origin.toNameOrNull()
        if (name == null) {
            dispatch(DocumentAction.SaveFailed(effect.documentId, "Unsupported origin: ${effect.origin.providerId}"))
            return
        }
        runCatching { LocalStorageDocumentStore.write(name, effect.text) }
            .onSuccess {
                dispatch(DocumentAction.SaveSucceeded(effect.documentId, effect.origin, effect.revision))
            }.onFailure { error ->
                dispatch(DocumentAction.SaveFailed(effect.documentId, error.message ?: "Cannot save document"))
            }
    }

    private fun readDocument(effect: GuiEffect.ReadDocument) {
        val name = effect.origin.toNameOrNull()
        val text = name?.let(LocalStorageDocumentStore::read)
        if (text == null) {
            dispatch(DocumentAction.ReloadFailed(effect.documentId, "Document not found in browser storage"))
        } else {
            dispatch(DocumentAction.ReloadSucceeded(effect.documentId, text))
        }
    }

    private fun confirmClose(effect: GuiEffect.ConfirmCloseDirtyPage) {
        val decision =
            if (window.confirm("Save changes to ${effect.displayName} before closing?")) {
                CloseDecision.SAVE
            } else if (window.confirm("Discard changes to ${effect.displayName}?")) {
                CloseDecision.DISCARD
            } else {
                CloseDecision.CANCEL
            }
        dispatch(WorkspaceAction.ClosePageDecisionProvided(effect.pageId, decision))
    }

    private fun confirmReload(effect: GuiEffect.ConfirmReloadDirtyDocument) {
        val decision =
            if (window.confirm("Discard local changes and reload ${effect.displayName}?")) {
                ReloadDecision.DISCARD_CHANGES
            } else {
                ReloadDecision.CANCEL
            }
        dispatch(DocumentAction.ReloadDecisionProvided(effect.documentId, decision))
    }

    private fun confirmExit(effect: GuiEffect.ConfirmExitWithDirtyDocuments) {
        val confirmed = window.confirm("${effect.dirtyDocuments.size} document(s) have unsaved changes. Discard them?")
        dispatch(if (confirmed) ApplicationAction.ExitConfirmed else ApplicationAction.ExitCancelled)
    }

    private fun copyToClipboard(text: String) {
        val area = document.createElement("textarea") as HTMLTextAreaElement
        area.value = text
        area.style.position = "fixed"
        area.style.opacity = "0"
        document.body?.appendChild(area)
        area.select()
        runCatching { document.execCommand("copy") }
        area.parentNode?.removeChild(area)
    }

    private fun dispatch(action: it.unibo.tuprolog.ui.gui.controller.GuiAction) {
        scope.launch { controller.dispatch(action) }
    }

    private fun String.toOrigin(): DocumentOrigin = DocumentOrigin(LOCAL_STORAGE_PROVIDER, this, this)

    private fun DocumentOrigin.toNameOrNull(): String? = opaqueReference.takeIf { providerId == LOCAL_STORAGE_PROVIDER }
}
