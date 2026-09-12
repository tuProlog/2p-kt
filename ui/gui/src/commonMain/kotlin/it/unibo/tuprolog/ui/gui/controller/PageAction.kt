package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.PageConfiguration
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import kotlin.time.Duration

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

    /** Discards concluded resolutions from the page's history. Any resolution still in progress is unaffected. */
    data class ClearHistory(
        override val pageId: PageId,
    ) : PageAction

    /**
     * Replaces the page's query history and resolution/solutions history wholesale, e.g. right after creating
     * a page from previously-persisted state - restoring what a frontend's Solutions tree and query-history
     * navigation show, without re-running anything.
     */
    data class RestoreHistory(
        override val pageId: PageId,
        val queryHistory: List<String> = emptyList(),
        val resolutions: List<ResolutionHistoryEntry> = emptyList(),
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
