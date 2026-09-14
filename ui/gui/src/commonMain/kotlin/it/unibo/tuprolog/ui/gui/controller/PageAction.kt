package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.PageConfiguration
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import kotlin.time.Duration

/** Actions scoped to one page: editing its query/scratch text/stdin/configuration, driving resolutions, and
 * managing its history/panels. */
sealed interface PageAction : GuiAction {
    /** The page this action applies to. */
    val pageId: PageId

    /** Edits a scratch page's in-memory text (does nothing for a document-backed page). */
    data class ChangeScratchText(
        override val pageId: PageId,
        val text: String,
    ) : PageAction

    /** Edits the page's current query text. */
    data class ChangeQuery(
        override val pageId: PageId,
        val query: String,
    ) : PageAction

    /** Replaces the standard input a solve on this page would read from. */
    data class ChangeStdin(
        override val pageId: PageId,
        val stdin: String,
    ) : PageAction

    /** Replaces the page's configuration (e.g. panel layout/visibility). */
    data class ChangeConfiguration(
        override val pageId: PageId,
        val configuration: PageConfiguration,
    ) : PageAction

    /** Switches the solver profile a page resolves queries against, invalidating its current session. */
    data class ChangeSolverProfile(
        override val pageId: PageId,
        val profileId: SolverProfileId,
    ) : PageAction

    /** Changes how long a resolution on this page may run before timing out. */
    data class ChangeTimeout(
        override val pageId: PageId,
        val timeout: Duration,
    ) : PageAction

    /** Starts resolving the page's current query, requesting up to [mode]'s solution count. */
    data class Solve(
        override val pageId: PageId,
        val mode: ConsumptionMode = ConsumptionMode.ONE,
    ) : PageAction

    /** Continues an in-progress resolution that's awaiting continuation, requesting up to [mode] more solutions. */
    data class Next(
        override val pageId: PageId,
        val mode: ConsumptionMode = ConsumptionMode.ONE,
    ) : PageAction

    /** Cancels the page's in-progress resolution, if any. */
    data class Stop(
        override val pageId: PageId,
    ) : PageAction

    /** Rebuilds the page's solver session from scratch, discarding any accumulated dynamic state. */
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

    /** Clears [panel]'s "unread changes" indicator, e.g. because the user just looked at it. */
    data class MarkPanelRead(
        override val pageId: PageId,
        val panel: PanelId,
    ) : PageAction

    /** Invokes one command an extension contributed, routed to that extension's action handler. */
    data class ExtensionCommand(
        override val pageId: PageId,
        val extensionId: ExtensionId,
        val commandId: CommandId,
        val payload: Map<String, String> = emptyMap(),
    ) : PageAction
}
