package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.LRUCache
import it.unibo.tuprolog.utils.impl.SimpleLRUCache

interface Cache<K, V> {
    val capacity: Int

    operator fun set(key: K, value: V): Optional<out K>

    operator fun get(key: K): V?

    fun toMap(): Map<K, V>

    fun toSequence(): Sequence<Pair<K, V>>

    companion object {
        fun <K, V> lru(capacity: Int = 5): Cache<K, V> = LRUCache(capacity)

        fun <K, V> simpleLru(capacity: Int = 5): Cache<K, V> = SimpleLRUCache(capacity)
    }
}