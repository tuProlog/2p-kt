package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.OutputStoreImpl
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface OutputStore : ChannelStore<String, OutputChannel<String>, OutputStore> {
    @JsName("stdOut")
    val stdOut: OutputChannel<String>

    @JsName("setStdOut")
    fun setStdOut(channel: OutputChannel<String>): OutputStore

    @JsName("stdErr")
    val stdErr: OutputChannel<String>

    @JsName("setStdErr")
    fun setStdErr(channel: OutputChannel<String>): OutputStore

    @JsName("warnings")
    val warnings: OutputChannel<Warning>

    @JsName("setWarnings")
    fun setWarnings(channel: OutputChannel<Warning>): OutputStore

    companion object {
        const val STDOUT = "stdout"

        const val STDERR = "stderr"

        @JsName("fromStandard")
        @JvmStatic
        @JvmOverloads
        fun fromStandard(
            output: OutputChannel<String> = OutputChannel.stdOut(),
            error: OutputChannel<String> = OutputChannel.stdErr(),
            warnings: OutputChannel<Warning> = OutputChannel.warn(),
        ): OutputStore = OutputStoreImpl(output, error, warnings, mapOf("user_output" to output))

        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(
            channels: Map<String, OutputChannel<String>>,
            warnings: OutputChannel<Warning> = OutputChannel.warn(),
        ): OutputStore {
            val stdOut = channels[STDOUT] ?: OutputChannel.stdOut()
            val stdErr = channels[STDERR] ?: OutputChannel.stdErr()
            return OutputStoreImpl(stdOut, stdErr, warnings, channels)
        }
    }
}
