package it.unibo.tuprolog.parser

internal class JsReadableStreamTextChunkSource(
    stream: Any,
) : SuspendingTextChunkSource {
    private val reader: dynamic = stream.asDynamic().getReader()
    private val decoder: dynamic = js("new TextDecoder('utf-8', { fatal: true })")
    private val streamingDecodeOptions: dynamic = js("({ stream: true })")

    private var finished: Boolean = false
    private var closed: Boolean = false

    @Suppress("ReturnCount")
    override suspend fun readChunk(): String? {
        if (finished) {
            return null
        }
        while (true) {
            val result = awaitPromise(reader.read()).asDynamic()
            if (result.done as Boolean) {
                finished = true
                val tail = decoder.decode() as String
                return if (tail.isEmpty()) null else tail
            }
            val decoded = decoder.decode(result.value, streamingDecodeOptions) as String
            if (decoded.isNotEmpty()) {
                return decoded
            }
        }
    }

    override suspend fun close() {
        if (closed) {
            return
        }
        closed = true
        if (!finished) {
            awaitPromise(reader.cancel())
        }
        reader.releaseLock()
    }
}
