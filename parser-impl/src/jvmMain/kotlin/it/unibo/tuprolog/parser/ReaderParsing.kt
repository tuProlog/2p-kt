package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import java.io.Reader

const val DEFAULT_READER_CHUNK_SIZE: Int = 8 * 1024

fun <T> buildParserFor(
    input: Reader,
    options: ParserOptions = ParserOptions(),
    continuation: (PrologParser, LexedSource) -> T,
): T = buildParserFor(ReaderToTextChunkSourceAdapter(input), options, continuation)
