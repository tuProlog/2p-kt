package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.ChannelStore
import it.unibo.tuprolog.unify.Unificator.Companion.matches

abstract class AbstractChannelStore<T : Any, C : Channel<T>, Self : ChannelStore<T, C, Self>>(
    protected val channels: Map<String, C>,
) : ChannelStore<T, C, Self>,
    Map<String, C> by channels {
    override fun toString(): String =
        channels.entries.joinToString(
            separator = ", ",
            prefix = "${this::class.simpleName}(",
            postfix = ")",
        ) {
            "${it.key}->${it.value}"
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AbstractChannelStore<*, *, *>

        if (channels != other.channels) return false

        return true
    }

    override fun hashCode(): Int = channels.hashCode()

    override fun findByTerm(streamTerm: Term): Sequence<C> =
        values.asSequence().filter { it.streamTerm matches streamTerm }

    override fun aliasesOf(channel: C): Sequence<String> =
        entries
            .asSequence()
            .filter { (_, v) -> v == channel }
            .map { it.key }
            .filterNot { it == ChannelStore.CURRENT }
}
