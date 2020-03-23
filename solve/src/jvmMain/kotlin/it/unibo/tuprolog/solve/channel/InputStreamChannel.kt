package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.AbstractInputChannel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class InputStreamChannel(inputStream: InputStream) : AbstractInputChannel<String>()  {

    private val reader: Reader by lazy {
        BufferedReader(InputStreamReader(inputStream))
    }

    override fun read(): String {
        return reader.readText()
    }

    override val available: Boolean
        get() = reader.ready()
}