package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractInputChannel
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class ReaderChannel(val reader: Reader) : AbstractInputChannel<String>() {

    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream))

    override fun readActually(): String = "${reader.read().toChar()}"

    override val available: Boolean
        @Synchronized
        get() = reader.ready()

    @Synchronized
    override fun close() {
        reader.close()
        super.close()
    }
}
