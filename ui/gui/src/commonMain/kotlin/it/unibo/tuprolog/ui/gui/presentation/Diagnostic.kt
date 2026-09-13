package it.unibo.tuprolog.ui.gui.presentation

/** One toolkit-neutral diagnostic (syntax error, solver warning, ...) shown against a document/page. */
data class Diagnostic(
    val severity: DiagnosticSeverity,
    val message: String,
    val range: TextRange? = null,
    val source: DiagnosticSource = DiagnosticSource("gui"),
    val code: String? = null,
)
