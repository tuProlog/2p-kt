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
) : InputStore, Map<String, InputChannel<String>> {

    private val inputChannels: Map<String, InputChannel<String>> = channels.toMutableMap()
        .ensureAliasRefersToChannel(STDIN, stdIn)
        .setCurrent(STDIN, stdIn)

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

    override fun minus(others: Sequence<String>): InputStore = InputStoreImpl(stdIn, inputChannels - others)

    override fun toString(): String =
        inputChannels.entries.joinToString(
            separator = ", ",
            prefix = "${this::class.simpleName}(",
            postfix = ")"
        ) {
            "${it.key}->${it.value}"
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as InputStoreImpl

        if (stdIn != other.stdIn) return false
        if (inputChannels != other.inputChannels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stdIn.hashCode()
        result = 31 * result + inputChannels.hashCode()
        return result
    }

    override val entries: Set<Map.Entry<String, InputChannel<String>>>
        get() = inputChannels.entries

    override val keys: Set<String>
        get() = inputChannels.keys

    override val size: Int
        get() = inputChannels.size

    override val values: Collection<InputChannel<String>>
        get() = inputChannels.values

    override fun containsKey(key: String): Boolean = inputChannels.containsKey(key)

    override fun containsValue(value: InputChannel<String>): Boolean = inputChannels.containsValue(value)

    override fun get(key: String): InputChannel<String>? = inputChannels[key]

    override fun isEmpty(): Boolean = inputChannels.isEmpty()
}
