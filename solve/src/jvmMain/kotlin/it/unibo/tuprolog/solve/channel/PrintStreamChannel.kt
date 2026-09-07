package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractOutputChannel
import java.io.PrintStream

/** A JVM [AbstractOutputChannel] writing every element to [printStream] via [PrintStream.print]. */
class PrintStreamChannel<T : Any>(
    private val printStream: PrintStream,
) : AbstractOutputChannel<T>() {
    override fun writeActually(value: T) = printStream.print(value)

    override fun flushActually() = printStream.flush()
}
