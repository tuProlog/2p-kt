package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.impl.lexer.RegexPrologLexer
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText

/**
 * Produces a lossless, lazy Prolog token stream independently of operator declarations.
 *
 * Word and graphic spellings are classified lexically; whether an occurrence is an atom, functor,
 * or operator is decided later by [PrologParser]. Trivia is retained, token spans are end-exclusive,
 * and source offsets count UTF-16 code units. Construction performs no input reads, so lexical and
 * source failures are deferred until the relevant token is requested.
 *
 * @see LexedSource
 * @see PrologParser
 */
interface PrologLexer {
    /**
     * Creates a lazy token source without consulting an operator table.
     *
     * Lexical failures are reported when the malformed construct is requested.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException when requested input is
     * lexically invalid or exceeds the configured retained-token limit
     */
    fun lex(
        source: SourceText,
        options: LexerOptions = LexerOptions(),
    ): LexedSource

    /**
     * Creates a lazy token source backed by synchronous text chunks.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException when requested text is
     * invalid, reading fails, or the retained-token limit is exceeded
     */
    fun lex(
        source: TextChunkSource,
        sourceId: String? = null,
        options: LexerOptions = LexerOptions(),
    ): LexedSource

    companion object {
        /** Returns the stateless default lexer implementation. */
        fun default(): PrologLexer = RegexPrologLexer()
    }
}
