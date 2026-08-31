package it.unibo.tuprolog.parser

/** A synchronous producer of text chunks. `null` denotes end of input. */
fun interface TextChunkSource {
    fun readChunk(): String?
}
