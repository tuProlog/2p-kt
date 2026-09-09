package it.unibo.tuprolog.ui.gui.presentation

data class SemanticToken(
    val range: TextRange,
    val category: SemanticCategory,
    val modifiers: Set<String> = emptySet(),
)
