package it.unibo.tuprolog.ui.gui.presentation

sealed interface SolutionPresentation {
    val query: String

    data class Yes(
        override val query: String,
        val bindings: List<BindingPresentation> = emptyList(),
        val solvedQuery: String? = null,
        val metadata: Map<String, String> = emptyMap(),
    ) : SolutionPresentation

    data class No(
        override val query: String,
    ) : SolutionPresentation

    data class Halt(
        override val query: String,
        val message: String,
        val logicStackTrace: List<String> = emptyList(),
        val isTimeout: Boolean = false,
    ) : SolutionPresentation
}
