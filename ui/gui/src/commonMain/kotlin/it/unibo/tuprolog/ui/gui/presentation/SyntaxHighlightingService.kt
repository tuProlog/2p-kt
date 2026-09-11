package it.unibo.tuprolog.ui.gui.presentation

fun interface SyntaxHighlightingService {
    suspend fun classify(
        source: String,
        operators: List<OperatorPresentation>,
    ): List<SemanticToken>
}
