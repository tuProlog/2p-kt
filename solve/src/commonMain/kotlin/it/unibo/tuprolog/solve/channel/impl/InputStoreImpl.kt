package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.ChannelStore.Companion.CURRENT_ALIAS
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.InputChannel

data class InputStoreImpl(
    private val inputChannels: Map<String, InputChannel<String>>
) : InputStore, Map<String, InputChannel<String>> by inputChannels {

    override fun setCurrent(alias: String): InputStore =
        when (val newCurrentChannel = get(alias)) {
            null -> this
            else -> InputStoreImpl(
                inputChannels.toMutableMap().also { it[CURRENT_ALIAS] = newCurrentChannel }
            )
        }

    override fun setCurrent(channel: InputChannel<String>): InputStore {
        val key = entries.firstOrNull { (_, v) -> v == channel }?.key
            ?: throw NoSuchElementException("Channel $channel has no alias")
        return setCurrent(key)
    }

    override fun plus(others: Map<String, InputChannel<String>>): InputStore =
        InputStoreImpl((this as Map<String, InputChannel<String>>) + others)

    override fun minus(others: Sequence<String>): InputStore = InputStoreImpl(inputChannels - others)

    override fun toString(): String {
        return inputChannels.entries.joinToString(
            separator = ", ",
            prefix = "${this::class.simpleName}(",
            postfix = ")"
        ) {
            "${it.key}->${it.value}"
        }
    }
}
