package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.ChannelStore.Companion.CURRENT_ALIAS
import it.unibo.tuprolog.solve.channel.impl.InputStoreImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface InputStore : ChannelStore<String, InputChannel<String>, InputStore> {
    companion object {
        const val STDIN = "\$stdin"

        @JsName("empty")
        @JvmStatic
        fun empty(): InputStore = InputStoreImpl(emptyMap())

        @JsName("default")
        @JvmStatic
        @JvmOverloads
        fun default(stdIn: InputChannel<String> = InputChannel.stdIn()): InputStore =
            InputStoreImpl(
                mapOf(STDIN to stdIn, CURRENT_ALIAS to stdIn),
            )

        @JsName("of")
        @JvmStatic
        fun of(channels: Map<String, InputChannel<String>>): InputStore = InputStoreImpl(channels)
    }

    @JsName("stdIn")
    val stdIn: InputChannel<String>?
        get() = this[STDIN]
}
