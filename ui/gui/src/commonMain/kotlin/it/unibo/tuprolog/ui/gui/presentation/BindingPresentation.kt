package it.unibo.tuprolog.ui.gui.presentation

/** One variable's value in a successful solution, already formatted as display-ready text. */
data class BindingPresentation(
    val variable: String,
    val value: String,
)
