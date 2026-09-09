package it.unibo.tuprolog.ui.swing

internal fun completionSuggestions(analysis: PrologAnalysis): List<PrologCompletion> =
    analysis.tokens
        .asReversed()
        .asSequence()
        .filter { it.category in setOf(PrologCategory.VARIABLE, PrologCategory.FUNCTOR, PrologCategory.ATOM) }
        .map { token ->
            PrologCompletion(
                analysis.source.substring(token.start, token.start + token.length),
                token.category.name.lowercase(),
            )
        }.distinctBy(PrologCompletion::replacement)
        .toList()
