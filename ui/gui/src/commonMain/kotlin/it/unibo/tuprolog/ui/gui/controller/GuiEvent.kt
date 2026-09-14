package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.ResolutionSessionId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

/** A discrete notification of something that just happened, broadcast via [GuiModel.events] - complementary to
 * [GuiModel.state] (which always holds the latest truth even if an event is missed by a late subscriber). */
sealed interface GuiEvent {
    /** The application finished starting up. */
    data object ApplicationStarted : GuiEvent

    /** The user asked to exit; a frontend may still veto this if documents are dirty. */
    data object ApplicationExitRequested : GuiEvent

    /** An exit request was vetoed/cancelled; the application keeps running. */
    data object ApplicationExitCancelled : GuiEvent

    /** A new page was created, optionally backed by [documentId] (`null` for a scratch page). */
    data class PageCreated(
        val pageId: PageId,
        val documentId: DocumentId?,
    ) : GuiEvent

    /** The workspace's selected page changed (`null` when no page is selected, e.g. the workspace is empty). */
    data class PageSelected(
        val pageId: PageId?,
    ) : GuiEvent

    /** A page was closed and removed from the workspace. */
    data class PageClosed(
        val pageId: PageId,
    ) : GuiEvent

    /** A new document was created (scratch or backed by a page). */
    data class DocumentCreated(
        val documentId: DocumentId,
    ) : GuiEvent

    /** A document's in-memory text changed, now at [revision]. */
    data class DocumentChanged(
        val documentId: DocumentId,
        val revision: Long,
    ) : GuiEvent

    /** A document was successfully persisted at [revision]. */
    data class DocumentSaved(
        val documentId: DocumentId,
        val revision: Long,
    ) : GuiEvent

    /** The user backed out of a save-destination picker; the document stays dirty. */
    data class DocumentSaveCancelled(
        val documentId: DocumentId,
    ) : GuiEvent

    /** Persisting a document failed with [message]. */
    data class DocumentSaveFailed(
        val documentId: DocumentId,
        val message: String,
    ) : GuiEvent

    /** A page started building a new solver session for [profileId] (e.g. after switching profiles). */
    data class SolverSessionBuilding(
        val pageId: PageId,
        val profileId: SolverProfileId,
    ) : GuiEvent

    /** A page's new solver session finished building and is ready to resolve queries. */
    data class SolverSessionReady(
        val pageId: PageId,
        val sessionId: SolverSessionId,
    ) : GuiEvent

    /** A page's solver session was invalidated (e.g. its source changed) and must be rebuilt before solving again. */
    data class SolverSessionInvalidated(
        val pageId: PageId,
    ) : GuiEvent

    /** A page started resolving [query]. */
    data class ResolutionStarted(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
        val query: String,
    ) : GuiEvent

    /** A resolution yielded one more [solution]. */
    data class SolutionProduced(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
        val solution: SolutionPresentation,
    ) : GuiEvent

    /** A resolution paused after a solution, waiting for a "next"/"stop" action before it can continue. */
    data class ResolutionAwaitingContinuation(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
    ) : GuiEvent

    /** A resolution ran to exhaustion with no further solutions. */
    data class ResolutionCompleted(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
    ) : GuiEvent

    /** A resolution was cancelled before completing; [resolutionId] is `null` if it was cancelled before one
     * was even assigned. */
    data class ResolutionCancelled(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId?,
    ) : GuiEvent

    /** A resolution failed with an unrecoverable error. */
    data class ResolutionFailed(
        val pageId: PageId,
        val resolutionId: ResolutionSessionId,
        val message: String,
    ) : GuiEvent

    /** A [GuiEffect] was queued for the hosting platform to perform. */
    data class EffectRequested(
        val effect: GuiEffect,
    ) : GuiEvent

    /** A dispatched [action] was rejected outright (e.g. it didn't apply to the current state) with [reason]. */
    data class ActionRejected(
        val action: GuiAction,
        val reason: String,
    ) : GuiEvent
}
