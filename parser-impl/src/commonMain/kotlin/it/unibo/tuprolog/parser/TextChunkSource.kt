package it.unibo.tuprolog.parser

/**
 * A synchronous, pull-based producer of text chunks.
 *
 * Empty chunks are permitted and `null` alone denotes end of input.
 */
fun interface TextChunkSource {
    /** Returns the next possibly empty chunk, or `null` when no input remains. */
    fun readChunk(): String?
}
