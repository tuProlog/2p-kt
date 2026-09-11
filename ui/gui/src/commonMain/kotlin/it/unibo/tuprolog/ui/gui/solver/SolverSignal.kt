package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation

sealed interface SolverSignal {
    data class Stdout(
        val text: String,
    ) : SolverSignal

    data class Stderr(
        val text: String,
    ) : SolverSignal

    data class Warning(
        val warning: WarningPresentation,
    ) : SolverSignal

    data class Diagnostics(
        val diagnostics: List<Diagnostic>,
    ) : SolverSignal

    data class Inspection(
        val snapshot: SolverInspectionSnapshot,
    ) : SolverSignal
}
