package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSource

data class DiagnosticState(
    val values: List<Diagnostic> = emptyList(),
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    val hasUnreadChanges: Boolean get() = revision > seenRevision

    /** Replaces only the diagnostics previously contributed by [source], leaving every other source untouched. */
    fun replaceSource(
        source: DiagnosticSource,
        diagnostics: List<Diagnostic>,
    ): DiagnosticState = copy(values = values.filterNot { it.source == source } + diagnostics, revision = revision + 1)

    fun markRead(): DiagnosticState = copy(seenRevision = revision)
}
