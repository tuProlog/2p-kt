package it.unibo.tuprolog.ui.gui.presentation

/** One solver flag's current value, already formatted as display-ready text. */
data class FlagPresentation(
    val name: String,
    val value: String,
    /** Markdown documentation, for a [it.unibo.tuprolog.solve.flags.NotableFlag]; blank otherwise. */
    val help: String = "",
)
