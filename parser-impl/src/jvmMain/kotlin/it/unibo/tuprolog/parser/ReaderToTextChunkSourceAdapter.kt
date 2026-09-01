package it.unibo.tuprolog.parser

import java.io.Reader

internal class ReaderToTextChunkSourceAdapter(
    private val reader: Reader,
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
    private val autoClose: Boolean = true,
) : TextChunkSource {
    init {
        require(chunkSize > 0) { "chunkSize must be positive" }
    }

    private val buffer by lazy {
        CharArray(chunkSize)
    }

    override fun readChunk(): String? {
        val count = reader.read(buffer)
        val result = if (count < 0) null else buffer.concatToString(0, count)
        if (result == null && autoClose) {
            reader.close()
        }
        return result
    }
}
