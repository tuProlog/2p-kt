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
) : OutputStore, Map<String, OutputChannel<String>> {

    private val outputChannels: Map<String, OutputChannel<String>> = outputChannels.toMutableMap()
        .ensureAliasRefersToChannel(STDOUT, stdOut)
        .ensureAliasRefersToChannel(STDERR, stdErr)
        .setCurrent(STDOUT, stdOut)

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
        OutputStoreImpl(stdOut, stdErr, warnings, outputChannels - others)

    override fun toString(): String =
        outputChannels.entries.joinToString(
            separator = ", ",
            prefix = "${this::class.simpleName}(",
            postfix = ")"
        ) {
            "${it.key}->${it.value}"
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as OutputStoreImpl

        if (stdOut != other.stdOut) return false
        if (stdErr != other.stdErr) return false
        if (warnings != other.warnings) return false
        if (outputChannels != other.outputChannels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stdOut.hashCode()
        result = 31 * result + stdErr.hashCode()
        result = 31 * result + warnings.hashCode()
        result = 31 * result + outputChannels.hashCode()
        return result
    }

    override val entries: Set<Map.Entry<String, OutputChannel<String>>>
        get() = outputChannels.entries

    override val keys: Set<String>
        get() = outputChannels.keys

    override val size: Int
        get() = outputChannels.size

    override val values: Collection<OutputChannel<String>>
        get() = outputChannels.values

    override fun containsKey(key: String): Boolean = outputChannels.containsKey(key)

    override fun containsValue(value: OutputChannel<String>): Boolean = outputChannels.containsValue(value)

    override fun get(key: String): OutputChannel<String>? = outputChannels[key]

    override fun isEmpty(): Boolean = outputChannels.isEmpty()
}
