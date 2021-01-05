package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.ChannelStore.Companion.CURRENT
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDERR
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDOUT
import it.unibo.tuprolog.solve.channel.impl.ChannelStoreUtils.ensureAliasRefersToChannel
import it.unibo.tuprolog.solve.channel.impl.ChannelStoreUtils.setCurrent
import it.unibo.tuprolog.solve.exception.PrologWarning

internal class OutputStoreImpl(
    override val stdOut: OutputChannel<String>,
    override val stdErr: OutputChannel<String>,
    override val warnings: OutputChannel<PrologWarning> = OutputChannel.warn(),
    outputChannels: Map<String, OutputChannel<String>> = emptyMap()
) : AbstractChannelStore<String, OutputChannel<String>, OutputStore>(
    outputChannels.toMutableMap()
        .ensureAliasRefersToChannel(STDOUT, stdOut)
        .ensureAliasRefersToChannel(STDERR, stdErr)
        .setCurrent(STDOUT, stdOut)
),
    OutputStore {

    override fun setCurrent(alias: String): OutputStore =
        when (val newCurrentChannel = get(alias)) {
            null -> this
            else -> OutputStoreImpl(stdOut, stdErr, warnings, mapOf(CURRENT to newCurrentChannel))
        }

    override fun setCurrent(channel: OutputChannel<String>): OutputStore {
        val key = entries.firstOrNull { (_, v) -> v == channel }?.key
            ?: throw NoSuchElementException("Channel $channel has no alias")
        return setCurrent(key)
    }

    override fun plus(others: Map<String, OutputChannel<String>>): OutputStore =
        OutputStoreImpl(stdOut, stdErr, warnings, (this as Map<String, OutputChannel<String>>) + others)

    override fun minus(others: Sequence<String>): OutputStore =
        OutputStoreImpl(stdOut, stdErr, warnings, channels - others)
}
