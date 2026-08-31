package it.unibo.tuprolog.parser

/** An asynchronous producer of text chunks. `null` denotes end of input. */
interface SuspendingTextChunkSource {
    suspend fun readChunk(): String?

    suspend fun close() {}
}
