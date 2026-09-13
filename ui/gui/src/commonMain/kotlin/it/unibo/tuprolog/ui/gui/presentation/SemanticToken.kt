package it.unibo.tuprolog.ui.gui.presentation

/** One classified span of source text, ready for a toolkit to render with syntax-highlighting colors. */
data class SemanticToken(
    val range: TextRange,
    val category: SemanticCategory,
    val modifiers: Set<String> = emptySet(),
)
