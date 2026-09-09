package it.unibo.tuprolog.ui.gui.presentation

data class LibraryPresentation(
    val alias: String,
    val predicates: List<String> = emptyList(),
    val operators: List<OperatorPresentation> = emptyList(),
    val functions: List<String> = emptyList(),
)
