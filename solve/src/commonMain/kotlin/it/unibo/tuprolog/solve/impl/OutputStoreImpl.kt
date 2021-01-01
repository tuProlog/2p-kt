package it.unibo.tuprolog.solve.impl

import it.unibo.tuprolog.solve.ChannelStore.Companion.CURRENT_ALIAS
import it.unibo.tuprolog.solve.OutputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning

data class OutputStoreImpl(
    private val outputChannels: Map<String, OutputChannel<String>>,
    override val warnings: OutputChannel<PrologWarning> = OutputChannel.warn()
) : OutputStore, Map<String, OutputChannel<String>> by outputChannels {

    override fun setCurrent(alias: String): OutputStore =
        when (val newCurrentChannel = get(alias)) {
            null -> this
            else -> {
                OutputStoreImpl(outputChannels.toMutableMap().also { it[CURRENT_ALIAS] = newCurrentChannel }, warnings)
            }
        }

    override fun setCurrent(channel: OutputChannel<String>): OutputStore {
        val key = entries.firstOrNull { (_, v) -> v == channel }?.key
            ?: throw NoSuchElementException("Channel $channel has no alias")
        return setCurrent(key)
    }

    override fun plus(others: Map<String, OutputChannel<String>>): OutputStore =
        OutputStoreImpl((this as Map<String, OutputChannel<String>>) + others, warnings)

    override fun minus(others: Sequence<String>): OutputStore =
        OutputStoreImpl(outputChannels - others, warnings)

    override fun toString(): String {
        return outputChannels.entries.joinToString(
            separator = ", ",
            prefix = "${this::class.simpleName}(",
            postfix = ")"
        ) {
            "${it.key}->${it.value}"
        }
    }
}
