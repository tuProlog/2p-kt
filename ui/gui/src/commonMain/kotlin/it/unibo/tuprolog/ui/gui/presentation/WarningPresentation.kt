package it.unibo.tuprolog.ui.gui.presentation

/** One solver warning, already formatted as a display-ready message and stack trace. */
data class WarningPresentation(
    val message: String,
    val logicStackTrace: List<String> = emptyList(),
)
