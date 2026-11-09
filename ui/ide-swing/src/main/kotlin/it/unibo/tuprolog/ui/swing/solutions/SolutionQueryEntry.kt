package it.unibo.tuprolog.ui.swing.solutions

import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

/** One raw query submitted on the current page, alongside the solutions it has produced so far. */
internal data class SolutionQueryEntry(
    val query: String,
    val solutions: List<SolutionPresentation>,
    val hasUnexploredPaths: Boolean,
    val submittedAt: Long? = null,
    val historyEntry: ResolutionHistoryEntry? = null,
)
