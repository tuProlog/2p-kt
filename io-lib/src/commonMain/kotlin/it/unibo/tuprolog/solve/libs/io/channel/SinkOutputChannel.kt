package it.unibo.tuprolog.solve.libs.io.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractOutputChannel
import okio.BufferedSink

/**
 * An [it.unibo.tuprolog.solve.channel.OutputChannel] writing UTF-8 text to an Okio [BufferedSink].
 */
internal class SinkOutputChannel(
    private val sink: BufferedSink,
) : AbstractOutputChannel<String>() {
    override fun writeActually(value: String) {
        sink.writeUtf8(value)
    }

    override fun flushActually() {
        sink.flush()
    }

    override fun close() {
        sink.close()
        super.close()
    }
}
