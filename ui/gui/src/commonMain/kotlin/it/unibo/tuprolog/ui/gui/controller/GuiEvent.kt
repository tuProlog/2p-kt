package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.ResolutionSessionId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

sealed interface GuiEvent {
    data object ApplicationStarted : GuiEvent

    data object ApplicationExitRequested : GuiEvent

    data object ApplicationExitCancelled : GuiEvent

    data class PageCreated(
        val pageId: PageId,
        val documentId: DocumentId?,
    ) : GuiEvent

    data class PageSelected(
        val pageId: PageId?,
    ) : GuiEvent

    data class PageClosed(
        val pageId: PageId,
    ) : GuiEvent

    data class DocumentCreated(
        val documentId: DocumentId,
    ) : GuiEvent

    data class DocumentChanged(
        val documentId: DocumentId,
        val revision: Long,
    ) : GuiEvent

    data class DocumentSaved(
        val documentId: DocumentId,
        val revision: Long,
    ) : GuiEvent

    data class DocumentSaveCancelled(
        val documentId: DocumentId,
    ) : GuiEvent

    data class DocumentSaveFailed(
        val documentId: DocumentId,
        val message: String,
    ) : GuiEvent

    data class SolverSessionBuilding(
        val pageId: PageId,
        val profileId: SolverProfileId,
    ) : GuiEvent

    data class SolverSessionReady(
        val pageId: PageId,
        val sessionId: SolverSessionId,
    ) : GuiEvent

    data class SolverSessionInvalidated(
        val pageId: PageId,
    ) : GuiEvent

    data class ResolutionStarted(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
        val query: String,
    ) : GuiEvent

    data class SolutionProduced(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
        val solution: SolutionPresentation,
    ) : GuiEvent

    data class ResolutionAwaitingContinuation(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
    ) : GuiEvent

    data class ResolutionCompleted(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
    ) : GuiEvent

    data class ResolutionCancelled(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId?,
    ) : GuiEvent

    data class ResolutionFailed(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
        val message: String,
    ) : GuiEvent

    data class EffectRequested(
        val effect: GuiEffect,
    ) : GuiEvent

    data class ActionRejected(
        val action: GuiAction,
        val reason: String,
    ) : GuiEvent
}
