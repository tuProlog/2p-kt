package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.PageId
import kotlin.time.Duration

/** Everything a `SolverSession` needs to open a `ResolutionCursor` for one query on one page. */
data class ResolutionRequest(
    val pageId: PageId,
    val query: String,
    val timeout: Duration,
    val options: Map<String, String>,
    val stdin: String,
)
