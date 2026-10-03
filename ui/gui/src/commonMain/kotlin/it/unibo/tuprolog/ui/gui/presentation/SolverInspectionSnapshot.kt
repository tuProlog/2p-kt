package it.unibo.tuprolog.ui.gui.presentation

/** Everything the inspector panels (operators, flags, libraries, knowledge bases) show about a solver at one
 * point in time, already formatted as display-ready text. */
data class SolverInspectionSnapshot(
    val operators: List<OperatorPresentation> = emptyList(),
    val flags: List<FlagPresentation> = emptyList(),
    val libraries: List<LibraryPresentation> = emptyList(),
    val staticKnowledgeBase: String = "",
    val dynamicKnowledgeBase: String = "",
)
