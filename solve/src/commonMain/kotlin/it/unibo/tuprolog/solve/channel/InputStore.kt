package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.InputStoreImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface InputStore : ChannelStore<String, InputChannel<String>, InputStore> {
    @JsName("stdIn")
    val stdIn: InputChannel<String>

    @JsName("setStdIn")
    fun setStdIn(channel: InputChannel<String>): InputStore

    companion object {
        const val STDIN = "stdin"

        @JsName("fromStandard")
        @JvmStatic
        @JvmOverloads
        fun fromStandard(input: InputChannel<String> = InputChannel.stdIn()): InputStore =
            InputStoreImpl(input, mapOf("user_input" to input))

        @JsName("of")
        @JvmStatic
        fun of(channels: Map<String, InputChannel<String>>): InputStore =
            if (STDIN in channels) {
                InputStoreImpl(channels[STDIN]!!, channels)
            } else {
                InputStoreImpl(InputChannel.stdIn(), channels)
            }
    }
}
