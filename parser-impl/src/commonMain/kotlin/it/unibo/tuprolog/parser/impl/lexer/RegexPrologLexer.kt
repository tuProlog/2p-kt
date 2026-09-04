package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.TextChunkSource
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText

internal class RegexPrologLexer : PrologLexer {
    override fun lex(
        source: SourceText,
        options: LexerOptions,
    ): LexedSource {
        var emitted = false
        return LazyLexedSource(
            source.id,
            TextChunkSource {
                if (emitted) {
                    null
                } else {
                    emitted = true
                    source.text
                }
            },
            options,
            source.origin,
        )
    }

    override fun lex(
        source: TextChunkSource,
        sourceId: String?,
        options: LexerOptions,
    ): LexedSource = LazyLexedSource(sourceId, source, options)
}
