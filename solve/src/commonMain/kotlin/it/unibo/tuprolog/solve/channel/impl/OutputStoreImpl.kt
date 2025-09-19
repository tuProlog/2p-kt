package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.ChannelStore.Companion.CURRENT
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDERR
import it.unibo.tuprolog.solve.channel.OutputStore.Companion.STDOUT
import it.unibo.tuprolog.solve.channel.impl.ChannelStoreUtils.ensureAliasRefersToChannel
import it.unibo.tuprolog.solve.channel.impl.ChannelStoreUtils.setCurrent
import it.unibo.tuprolog.solve.exception.Warning

internal class OutputStoreImpl(
    override val stdOut: OutputChannel<String>,
    override val stdErr: OutputChannel<String>,
    override val warnings: OutputChannel<Warning> = OutputChannel.warn(),
    outputChannels: Map<String, OutputChannel<String>> = emptyMap(),
) : AbstractChannelStore<String, OutputChannel<String>, OutputStore>(checkChannels(stdOut, stdErr, outputChannels)),
    OutputStore {
    override fun setStdOut(channel: OutputChannel<String>): OutputStore =
        (channels - STDOUT).let {
            if (current == stdOut) {
                OutputStoreImpl(channel, stdErr, warnings, it + (CURRENT to channel))
            } else {
                OutputStoreImpl(channel, stdErr, warnings, it)
            }
        }

    override fun setStdErr(channel: OutputChannel<String>): OutputStore =
        (channels - STDERR).let {
            if (current == stdErr) {
                OutputStoreImpl(stdOut, channel, warnings, it + (CURRENT to channel))
            } else {
                OutputStoreImpl(stdOut, channel, warnings, it)
            }
        }

    override fun setWarnings(channel: OutputChannel<Warning>): OutputStore =
        OutputStoreImpl(
            stdOut,
            stdErr,
            channel,
            channels,
        )

    override fun setCurrent(alias: String): OutputStore =
        when (val newCurrentChannel = get(alias)) {
            null -> this
            else -> OutputStoreImpl(stdOut, stdErr, warnings, mapOf(CURRENT to newCurrentChannel))
        }

    override fun setCurrent(channel: OutputChannel<String>): OutputStore {
        val key =
            entries.firstOrNull { (_, v) -> v == channel }?.key
                ?: throw NoSuchElementException("Channel $channel has no alias")
        return setCurrent(key)
    }

    override fun plus(others: Map<String, OutputChannel<String>>): OutputStore =
        OutputStoreImpl(stdOut, stdErr, warnings, (this as Map<String, OutputChannel<String>>) + others)

    override fun minus(others: Sequence<String>): OutputStore =
        OutputStoreImpl(
            stdOut,
            stdErr,
            warnings,
            channels - others,
        )

    companion object {
        private fun checkChannels(
            stdOut: OutputChannel<String>,
            stdErr: OutputChannel<String>,
            channels: Map<String, OutputChannel<String>>,
        ): Map<String, OutputChannel<String>> =
            channels
                .toMutableMap()
                .ensureAliasRefersToChannel(STDOUT, stdOut)
                .ensureAliasRefersToChannel(STDERR, stdErr)
                .setCurrent(STDOUT, stdOut)
    }
}
