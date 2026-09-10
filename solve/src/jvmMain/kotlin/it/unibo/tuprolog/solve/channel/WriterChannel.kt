package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractOutputChannel
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer

/** A JVM [AbstractOutputChannel] writing every [String] to [writer] (or to [outputStream], wrapped into one). */
class WriterChannel(
    private val writer: Writer,
) : AbstractOutputChannel<String>() {
    constructor(outputStream: OutputStream) : this(OutputStreamWriter(outputStream))

    override fun writeActually(value: String) = writer.write(value)

    override fun flushActually() = writer.flush()

    @Synchronized
    override fun close() {
        writer.close()
        super.close()
    }
}
