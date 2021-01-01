package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.OutputStoreImpl
import it.unibo.tuprolog.solve.exception.PrologWarning
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface OutputStore : ChannelStore<String, OutputChannel<String>, OutputStore> {
    companion object {
        const val STDOUT = "\$stdout"

        const val STDERR = "\$stderr"

        @JsName("default")
        @JvmStatic
        @JvmOverloads
        fun default(
            stdOut: OutputChannel<String> = OutputChannel.stdOut(),
            stdErr: OutputChannel<String> = OutputChannel.stdErr(),
            warnings: OutputChannel<PrologWarning> = OutputChannel.warn()
        ): OutputStore = OutputStoreImpl(stdOut, stdErr, warnings)

        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(
            channels: Map<String, OutputChannel<String>>,
            warnings: OutputChannel<PrologWarning> = OutputChannel.warn()
        ): OutputStore {
            val stdOut = channels[STDOUT] ?: OutputChannel.stdOut()
            val stdErr = channels[STDERR] ?: OutputChannel.stdErr()
            return OutputStoreImpl(stdOut, stdErr, warnings, channels)
        }
    }

    @JsName("stdOut")
    val stdOut: OutputChannel<String>

    @JsName("stdErr")
    val stdErr: OutputChannel<String>

    @JsName("warnings")
    val warnings: OutputChannel<PrologWarning>
}
