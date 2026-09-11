package it.unibo.tuprolog.ui.gui.presentation

data class SolverInspectionSnapshot(
    val operators: List<OperatorPresentation> = emptyList(),
    val flags: List<FlagPresentation> = emptyList(),
    val libraries: List<LibraryPresentation> = emptyList(),
    val staticKnowledgeBase: String = "",
    val dynamicKnowledgeBase: String = "",
)
