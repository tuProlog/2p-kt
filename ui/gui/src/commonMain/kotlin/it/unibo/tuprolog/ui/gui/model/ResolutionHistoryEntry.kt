package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

/** One concluded resolution recorded in a page's history: the query it ran, every solution it yielded, and how
 * it ended. */
data class ResolutionHistoryEntry(
    val query: String,
    val solutions: List<SolutionPresentation>,
    val terminalStatus: ResolutionStatus,
    val error: String? = null,
)
