package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.ChannelStore

internal object ChannelStoreUtils {
    fun <T : Any, C : Channel<T>> MutableMap<String, C>.ensureAliasRefersToChannel(
        key: String,
        channel: C,
    ): MutableMap<String, C> {
        this[key] = channel
        return this
    }

    fun <T : Any, C : Channel<T>> MutableMap<String, C>.setCurrent(
        key: String,
        defaultChannel: C,
    ): MutableMap<String, C> {
        if (ChannelStore.CURRENT !in this) {
            this[ChannelStore.CURRENT] = this[key] ?: defaultChannel
        }
        return this
    }

    /**
     * These channels, where the [CURRENT][ChannelStore.CURRENT] alias and the [standardAlias] (e.g. `user_output`)
     * are moved from [old] to [new], if they named [old]: replacing a standard channel carries along the aliases that
     * refer to it.
     */
    fun <T : Any, C : Channel<T>> Map<String, C>.rebindingStandardAliases(
        standardAlias: String,
        old: C,
        new: C,
    ): Map<String, C> =
        mapValues { (alias, channel) ->
            if ((alias == ChannelStore.CURRENT || alias == standardAlias) && channel == old) new else channel
        }
}
