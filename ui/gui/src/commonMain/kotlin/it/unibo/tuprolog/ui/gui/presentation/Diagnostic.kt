package it.unibo.tuprolog.ui.gui.presentation

data class Diagnostic(
    val severity: DiagnosticSeverity,
    val message: String,
    val range: TextRange? = null,
    val source: DiagnosticSource = DiagnosticSource("gui"),
    val code: String? = null,
)
