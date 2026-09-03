package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.EffectId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.ResolutionSessionId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageConfiguration
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface GuiModel {
    val state: StateFlow<GuiState>
    val events: SharedFlow<GuiEvent>

    /** Lossless, ordered, single-consumer requests to the hosting platform. */
    val effects: Flow<GuiEffect>
}

interface GuiController : GuiModel {
    suspend fun dispatch(action: GuiAction)

    suspend fun shutdown()
}

sealed interface GuiAction

sealed interface ApplicationAction : GuiAction {
    data object Start : ApplicationAction

    data object RequestExit : ApplicationAction

    data object ExitConfirmed : ApplicationAction

    data object ExitCancelled : ApplicationAction
}

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
    ) : WorkspaceAction

    data class RequestClosePage(
        val pageId: PageId,
    ) : WorkspaceAction

    data class ClosePageDecisionProvided(
        val pageId: PageId,
        val decision: CloseDecision,
    ) : WorkspaceAction
}

enum class CloseDecision {
    SAVE,
    DISCARD,
    CANCEL,
}

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

enum class ReloadDecision {
    DISCARD_CHANGES,
    CANCEL,
}

enum class ConsumptionMode {
    ONE,
    ALL,
}

sealed interface PageAction : GuiAction {
    val pageId: PageId

    data class ChangeScratchText(
        override val pageId: PageId,
        val text: String,
    ) : PageAction

    data class ChangeQuery(
        override val pageId: PageId,
        val query: String,
    ) : PageAction

    data class ChangeStdin(
        override val pageId: PageId,
        val stdin: String,
    ) : PageAction

    data class ChangeConfiguration(
        override val pageId: PageId,
        val configuration: PageConfiguration,
    ) : PageAction

    data class ChangeSolverProfile(
        override val pageId: PageId,
        val profileId: SolverProfileId,
    ) : PageAction

    data class ChangeTimeout(
        override val pageId: PageId,
        val timeout: Duration,
    ) : PageAction

    data class Solve(
        override val pageId: PageId,
        val mode: ConsumptionMode = ConsumptionMode.ONE,
    ) : PageAction

    data class Next(
        override val pageId: PageId,
        val mode: ConsumptionMode = ConsumptionMode.ONE,
    ) : PageAction

    data class Stop(
        override val pageId: PageId,
    ) : PageAction

    data class Reset(
        override val pageId: PageId,
    ) : PageAction

    data class MarkPanelRead(
        override val pageId: PageId,
        val panel: PanelId,
    ) : PageAction

    data class ExtensionCommand(
        override val pageId: PageId,
        val extensionId: ExtensionId,
        val commandId: CommandId,
        val payload: Map<String, String> = emptyMap(),
    ) : PageAction
}

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
