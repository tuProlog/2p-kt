package it.unibo.tuprolog.ui.gui.presentation

/** One loaded library's contents, already formatted as display-ready text/signatures, for the libraries
 * inspector panel. */
data class LibraryPresentation(
    val alias: String,
    val predicates: List<String> = emptyList(),
    val operators: List<OperatorPresentation> = emptyList(),
    val functions: List<String> = emptyList(),
)
