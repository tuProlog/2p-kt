package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.ChannelStore.Companion.CURRENT_ALIAS
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.impl.OutputStoreImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface OutputStore : ChannelStore<String, OutputChannel<String>, OutputStore> {
    companion object {
        const val STDOUT = "\$stdin"

        const val STDERR = "\$stderr"

        @JsName("empty")
        @JvmStatic
        fun empty(): OutputStore = OutputStoreImpl(emptyMap())

        @JsName("default")
        @JvmStatic
        @JvmOverloads
        fun default(
            stdOut: OutputChannel<String> = OutputChannel.stdOut(),
            stdErr: OutputChannel<String> = OutputChannel.stdErr(),
            warnings: OutputChannel<PrologWarning> = OutputChannel.warn()
        ): OutputStore =
            OutputStoreImpl(
                outputChannels = mapOf(STDOUT to stdOut, CURRENT_ALIAS to stdOut, STDERR to stdErr),
                warnings = warnings
            )

        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(
            channels: Map<String, OutputChannel<String>>,
            warnings: OutputChannel<PrologWarning> = OutputChannel.warn()
        ): OutputStore = OutputStoreImpl(channels, warnings)
    }

    @JsName("stdOut")
    val stdOut: OutputChannel<String>?
        get() = this[STDOUT]

    @JsName("stdErr")
    val stdErr: OutputChannel<String>?
        get() = this[STDERR]

    @JsName("warnings")
    val warnings: OutputChannel<PrologWarning>
}
