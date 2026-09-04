package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import java.io.Reader

/** Default number of UTF-16 code units requested from a JVM [Reader] at once. */
const val DEFAULT_READER_CHUNK_SIZE: Int = 8 * 1024

/**
 * Creates a lazy lexer and parser over [input], then invokes [continuation].
 *
 * ```kotlin
 * FileReader("program.pl").use { reader ->
 *     buildParserFor(reader, autoClose = false) { parser, source ->
 *         parser.parseTheory(source)
 *     }
 * }
 * ```
 *
 * [autoClose] controls whether EOF closes [input]. Reading and lexing still occur only as tokens
 * are requested by [continuation].
 *
 * @return the value returned by [continuation]
 * @throws IllegalArgumentException if [chunkSize] is not positive
 * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if reading, lexing, or parsing
 * requested input fails
 */
@Suppress("LongParameterList")
fun <T> buildParserFor(
    input: Reader,
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
    autoClose: Boolean = true,
    lexerOptions: LexerOptions = LexerOptions(),
    parserOptions: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T = buildParserFor(input.toSource(chunkSize, autoClose), lexerOptions, parserOptions, continuation)

/**
 * Adapts this reader to a pull-based [TextChunkSource].
 *
 * Empty reader results are exposed as empty chunks rather than EOF. When [autoClose] is true, this
 * reader is closed after its first EOF result.
 *
 * @throws IllegalArgumentException if [chunkSize] is not positive
 */
fun Reader.toSource(
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
    autoClose: Boolean = true,
): TextChunkSource = ReaderToTextChunkSourceAdapter(this, chunkSize, autoClose)
