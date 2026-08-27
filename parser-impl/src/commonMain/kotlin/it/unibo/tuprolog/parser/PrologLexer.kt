package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.impl.lexer.RegexPrologLexer
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText

interface PrologLexer {
    /**
     * Lexes the complete input without consulting an operator table.
     *
     * @throws PrologLexingException on the first malformed lexical construct
     */
    fun lex(source: SourceText): LexedSource

    companion object {
        fun default(): PrologLexer = RegexPrologLexer()
    }
}
