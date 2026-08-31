package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.impl.lexer.RegexPrologLexer
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText

interface PrologLexer {
    /**
     * Creates a lazy token source without consulting an operator table.
     *
     * Lexical failures are reported when the malformed construct is requested.
     */
    fun lex(
        source: SourceText,
        options: LexerOptions = LexerOptions(),
    ): LexedSource

    /** Creates a lazy token source backed by synchronous text chunks. */
    fun lex(
        source: TextChunkSource,
        sourceId: String? = null,
        options: LexerOptions = LexerOptions(),
    ): LexedSource

    companion object {
        fun default(): PrologLexer = RegexPrologLexer()
    }
}
