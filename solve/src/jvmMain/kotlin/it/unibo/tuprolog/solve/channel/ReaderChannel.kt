package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractInputChannel
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class ReaderChannel(val reader: Reader) : AbstractInputChannel<String>() {

    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream))

    override fun readActually(): String? =
        reader.read().takeIf { it >= 0 }?.toChar()?.toString()

    override val available: Boolean
        @Synchronized
        get() = try {
            reader.ready()
        } catch (_: IOException) {
            false
        }

    @Synchronized
    override fun close() {
        reader.close()
        super.close()
    }
}
