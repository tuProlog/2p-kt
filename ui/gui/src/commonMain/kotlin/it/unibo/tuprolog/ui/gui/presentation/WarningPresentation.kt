package it.unibo.tuprolog.ui.gui.presentation

data class WarningPresentation(
    val message: String,
    val logicStackTrace: List<String> = emptyList(),
)
