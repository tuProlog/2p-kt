package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.InputStoreImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A [ChannelStore] of [InputChannel]s of [String]s, as held by a [it.unibo.tuprolog.solve.Solver] (see
 * [it.unibo.tuprolog.solve.ExecutionContextAware.inputChannels]).
 */
interface InputStore : ChannelStore<String, InputChannel<String>, InputStore> {
    /** The channel aliased [STDIN], i.e. this store's standard input. */
    @JsName("stdIn")
    val stdIn: InputChannel<String>

    /** Returns a copy of this store with [stdIn] (and the [STDIN] alias) replaced by [channel]. */
    @JsName("setStdIn")
    fun setStdIn(channel: InputChannel<String>): InputStore

    companion object {
        /** The reserved alias for the standard input channel. */
        const val STDIN = "stdin"

        /** Creates an [InputStore] with [input] (defaulting to [InputChannel.stdIn]) as both [stdIn] and `"user_input"`. */
        @JsName("fromStandard")
        @JvmStatic
        @JvmOverloads
        fun fromStandard(input: InputChannel<String> = InputChannel.stdIn()): InputStore =
            InputStoreImpl(input, mapOf("user_input" to input))

        /** Creates an [InputStore] out of [channels], using the [STDIN]-aliased one (or [InputChannel.stdIn] if absent) as [stdIn]. */
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
