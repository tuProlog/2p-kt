package it.unibo.tuprolog.parser

/**
 * An asynchronous, pull-based producer of text chunks.
 *
 * Empty chunks are permitted and `null` alone denotes end of input.
 */
interface SuspendingTextChunkSource {
    /** Returns the next possibly empty chunk, or `null` when no input remains. */
    suspend fun readChunk(): String?

    /** Releases source resources; the default implementation does nothing. */
    suspend fun close() {}
}
