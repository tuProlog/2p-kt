package it.unibo.tuprolog.ui.gui.presentation

/** A candidate identifier to offer for auto-completion, derived from already-classified [SemanticToken]s. */
data class SourceSuggestion(
    val text: String,
    val category: String,
)

/** Ranks identifiers already present in [analysis] as completion candidates, most recent first. */
fun sourceIdentifierSuggestions(analysis: SyntaxAnalysis): List<SourceSuggestion> =
    analysis.tokens
        .asReversed()
        .asSequence()
        .filter { it.category in setOf(SemanticCategory.VARIABLE, SemanticCategory.FUNCTOR, SemanticCategory.ATOM) }
        .map { token ->
            SourceSuggestion(
                analysis.source.substring(token.range.start.offset, token.range.endExclusive.offset),
                token.category.name.lowercase(),
            )
        }.distinctBy(SourceSuggestion::text)
        .toList()
