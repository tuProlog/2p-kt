package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractInputChannel
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

/**
 * A JVM [AbstractInputChannel] reading one character at a time from [reader] (or from [inputStream], wrapped into
 * one), reporting [available] as `false` (rather than throwing) if the underlying [Reader] raises an [IOException].
 */
class ReaderChannel(
    val reader: Reader,
) : AbstractInputChannel<String>() {
    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream))

    // a character beyond the Basic Multilingual Plane is two UTF-16 chars, read together
    override fun readActually(): String? {
        val first = readChar()
        val second = if (first?.isHighSurrogate() == true) readChar() else null
        return first?.let { if (second == null) "$it" else "$it$second" }
    }

    private fun readChar(): Char? = reader.read().takeIf { it >= 0 }?.toChar()

    override val available: Boolean
        @Synchronized
        get() =
            try {
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
