package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation

data class SolverProfile(
    val id: SolverProfileId,
    val displayName: String,
    val capabilities: SolverCapabilities,
    val factory: SolverSessionFactory,
    val defaultOptions: Map<String, String> = emptyMap(),
    /**
     * The operators a solver built from this profile is known to support before any session/resolution ever
     * runs (e.g. a dialect's own operators, like ProbLog's `::`) - seeds a new page's syntax highlighting
     * (see `DefaultGuiController.newPage`) so it doesn't flag a dialect's own valid operators as syntax errors
     * until the first solve happens to build a real solver and refresh it.
     */
    val defaultOperators: List<OperatorPresentation> = emptyList(),
)
