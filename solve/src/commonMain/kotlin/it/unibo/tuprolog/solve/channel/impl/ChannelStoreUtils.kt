package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.ChannelStore

internal object ChannelStoreUtils {
    fun <T, C : Channel<T>> MutableMap<String, C>.ensureAliasRefersToChannel(key: String, channel: C): MutableMap<String, C> {
        this[key] = channel
        return this
    }

    fun <T, C : Channel<T>> MutableMap<String, C>.setCurrent(key: String, defaultChannel: C): MutableMap<String, C> {
        if (ChannelStore.CURRENT !in this) {
            this[ChannelStore.CURRENT] = this[key] ?: defaultChannel
        }
        return this
    }
}
