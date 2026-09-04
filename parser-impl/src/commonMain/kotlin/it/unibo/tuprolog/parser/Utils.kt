package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText

/**
 * Creates the default lexer and parser for [source], then invokes [continuation] with both.
 *
 * This helper does not translate exceptions thrown by lexing, parsing, or [continuation].
 *
 * @return the value returned by [continuation]
 * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if requested source text is
 * invalid
 */
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

/**
 * Creates the default lexer and parser for a synchronous chunk [source].
 *
 * Input remains lazy and is pulled only when [continuation] requests tokens.
 *
 * @return the value returned by [continuation]
 * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if reading, lexing, or parsing
 * requested input fails
 */
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

/**
 * Creates the default lexer and parser for [input], identified diagnostically by [id].
 *
 * ```kotlin
 * val tree = buildParserFor("f(X).", id = "query") { parser, source ->
 *     parser.parseClause(source)
 * }
 * ```
 *
 * @return the value returned by [continuation]
 * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if requested input is invalid
 */
fun <T> buildParserFor(
    input: String,
    id: String? = null,
    lexerOptions: LexerOptions = LexerOptions(),
    parserOptions: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T = buildParserFor(SourceText(input, id), lexerOptions, parserOptions, continuation)
