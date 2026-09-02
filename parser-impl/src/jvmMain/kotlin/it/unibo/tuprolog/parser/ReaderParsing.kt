package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import java.io.Reader

const val DEFAULT_READER_CHUNK_SIZE: Int = 8 * 1024

@Suppress("LongParameterList")
fun <T> buildParserFor(
    input: Reader,
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
    autoClose: Boolean = true,
    lexerOptions: LexerOptions = LexerOptions(),
    parserOptions: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T = buildParserFor(input.toSource(chunkSize, autoClose), lexerOptions, parserOptions, continuation)

fun Reader.toSource(
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
    autoClose: Boolean = true,
): TextChunkSource = ReaderToTextChunkSourceAdapter(this, chunkSize, autoClose)
