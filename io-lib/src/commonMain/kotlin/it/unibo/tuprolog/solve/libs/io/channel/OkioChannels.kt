package it.unibo.tuprolog.solve.libs.io.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractInputChannel
import it.unibo.tuprolog.solve.channel.impl.AbstractOutputChannel
import okio.BufferedSink
import okio.BufferedSource

/**
 * An [it.unibo.tuprolog.solve.channel.InputChannel] reading UTF-8 text, one UTF-16 code unit at a
 * time (consistently with the other [it.unibo.tuprolog.solve.channel.InputChannel] implementations),
 * off an Okio [BufferedSource].
 */
internal class SourceInputChannel(
    private val source: BufferedSource,
) : AbstractInputChannel<String>() {
    private val pendingChars = ArrayDeque<Char>()

    override fun readActually(): String? {
        if (pendingChars.isEmpty()) {
            if (source.exhausted()) return null
            val codePoint = source.readUtf8CodePoint()
            if (codePoint > Char.MAX_VALUE.code) {
                val offset = codePoint - 0x10000
                pendingChars.addLast(((offset shr 10) + 0xD800).toChar())
                pendingChars.addLast(((offset and 0x3FF) + 0xDC00).toChar())
            } else {
                pendingChars.addLast(codePoint.toChar())
            }
        }
        return pendingChars.removeFirst().toString()
    }

    override fun close() {
        source.close()
        super.close()
    }
}

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
