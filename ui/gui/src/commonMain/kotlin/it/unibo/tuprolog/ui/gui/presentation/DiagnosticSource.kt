package it.unibo.tuprolog.ui.gui.presentation

/** Identifies what produced a [Diagnostic] (see [DiagnosticSources] for the well-known ones), so
 * `DiagnosticState.replaceSource` can replace one source's diagnostics without touching another's. */
data class DiagnosticSource(
    val value: String,
)
