package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic

internal data class PrologAnalysis(
    val source: String,
    val tokens: List<ColouredToken>,
    val diagnostics: List<Diagnostic>,
)
