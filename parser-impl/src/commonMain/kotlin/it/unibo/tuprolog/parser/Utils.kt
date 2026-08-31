package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText

fun <T> buildParserFor(
    source: SourceText,
    lexerOptions: LexerOptions = LexerOptions(),
    parserOptions: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T {
    val lexer = PrologLexer.default()
    val lexedSource = lexer.lex(source, lexerOptions)
    val parser = PrologParser.default(parserOptions)
    return continuation(parser, lexedSource)
}

fun <T> buildParserFor(
    source: TextChunkSource,
    lexerOptions: LexerOptions = LexerOptions(),
    parserOptions: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T {
    val lexer = PrologLexer.default()
    val lexedSource = lexer.lex(source, options = lexerOptions)
    val parser = PrologParser.default(parserOptions)
    return continuation(parser, lexedSource)
}

fun <T> buildParserFor(
    input: String,
    id: String? = null,
    lexerOptions: LexerOptions = LexerOptions(),
    parserOptions: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T = buildParserFor(SourceText(input, id), lexerOptions, parserOptions, continuation)
