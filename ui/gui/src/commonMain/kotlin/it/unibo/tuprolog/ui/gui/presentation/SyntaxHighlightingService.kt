package it.unibo.tuprolog.ui.gui.presentation

/** Toolkit-neutral entry point for turning source text into semantic tokens, so a frontend doesn't need to
 * depend on `PrologSyntaxAnalyzer` directly. */
fun interface SyntaxHighlightingService {
    /** Classifies [source] (aware of [operators]) into the semantic tokens a frontend renders as highlighting. */
    suspend fun classify(
        source: String,
        operators: List<OperatorPresentation>,
    ): List<SemanticToken>
}
