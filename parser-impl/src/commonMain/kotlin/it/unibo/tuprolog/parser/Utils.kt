package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText

fun <T> buildParserFor(
    source: SourceText,
    options: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T {
    val lexer = PrologLexer.default()
    val lexedSource = lexer.lex(source)
    val parser = PrologParser.default(options)
    return continuation(parser, lexedSource)
}

fun <T> buildParserFor(
    source: TextChunkSource,
    options: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T {
    val lexer = PrologLexer.default()
    val lexedSource = lexer.lex(source)
    val parser = PrologParser.default(options)
    return continuation(parser, lexedSource)
}

fun <T> buildParserFor(
    input: String,
    id: String? = null,
    options: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T = buildParserFor(SourceText(input, id), options, continuation)
