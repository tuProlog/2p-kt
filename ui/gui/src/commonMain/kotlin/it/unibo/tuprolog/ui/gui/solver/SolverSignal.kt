package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation

/** Something a solver emitted while resolving a query, alongside (not instead of) its `ResolutionStep`s. */
sealed interface SolverSignal {
    /** A chunk of standard-output text produced by the query. */
    data class Stdout(
        val text: String,
    ) : SolverSignal

    /** A chunk of standard-error text produced by the query. */
    data class Stderr(
        val text: String,
    ) : SolverSignal

    /** A non-fatal warning raised during resolution. */
    data class Warning(
        val warning: WarningPresentation,
    ) : SolverSignal

    /** Static/syntax diagnostics produced alongside the resolution (e.g. re-checking the source). */
    data class Diagnostics(
        val diagnostics: List<Diagnostic>,
    ) : SolverSignal

    /** The solver's inspectable state changed (e.g. a directive altered flags/knowledge base) and should be
     * re-read by whatever displays it. */
    data class Inspection(
        val snapshot: SolverInspectionSnapshot,
    ) : SolverSignal
}
