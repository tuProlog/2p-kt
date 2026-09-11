package it.unibo.tuprolog.ui.gui.presentation

/** Result of analysing [source]: its classified tokens and any syntax diagnostics found along the way. */
data class SyntaxAnalysis(
    val source: String,
    val tokens: List<SemanticToken>,
    val diagnostics: List<Diagnostic>,
)
