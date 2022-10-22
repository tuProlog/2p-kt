package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.ChannelStore.Companion.CURRENT
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.InputStore.Companion.STDIN
import it.unibo.tuprolog.solve.channel.impl.ChannelStoreUtils.ensureAliasRefersToChannel
import it.unibo.tuprolog.solve.channel.impl.ChannelStoreUtils.setCurrent

internal class InputStoreImpl(
    override val stdIn: InputChannel<String>,
    channels: Map<String, InputChannel<String>> = emptyMap()
) : InputStore, AbstractChannelStore<String, InputChannel<String>, InputStore>(
    channels.toMutableMap()
        .ensureAliasRefersToChannel(STDIN, stdIn)
        .setCurrent(STDIN, stdIn)
) {
    override fun setStdIn(channel: InputChannel<String>): InputStore =
        (channels - STDIN).let {
            if (current == stdIn) {
                InputStoreImpl(channel, it + (CURRENT to channel))
            } else {
                InputStoreImpl(channel, it)
            }
        }

    override fun setCurrent(alias: String): InputStore =
        when (val newCurrentChannel = get(alias)) {
            null -> this
            else -> InputStoreImpl(stdIn, mapOf(CURRENT to newCurrentChannel))
        }

    override fun setCurrent(channel: InputChannel<String>): InputStore {
        val key = entries.firstOrNull { (_, v) -> v == channel }?.key
            ?: throw NoSuchElementException("Channel $channel has no alias")
        return setCurrent(key)
    }

    override fun plus(others: Map<String, InputChannel<String>>): InputStore =
        InputStoreImpl(stdIn, (this as Map<String, InputChannel<String>>) + others)

    override fun minus(others: Sequence<String>): InputStore = InputStoreImpl(stdIn, channels - others)
}
