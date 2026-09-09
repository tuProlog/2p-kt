package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation

data class ResolutionHistoryEntry(
    val query: String,
    val solutions: List<SolutionPresentation>,
    val terminalStatus: ResolutionStatus,
    val error: String? = null,
)
