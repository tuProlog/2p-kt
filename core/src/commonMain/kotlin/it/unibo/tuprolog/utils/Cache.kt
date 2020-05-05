package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.LRUCache

interface Cache<K, V> {
    val capacity: Int

    operator fun set(key: K, value: V): K?

    operator fun get(key: K): V?

    fun toMap(): Map<K, V>

    fun toSequence(): Sequence<Pair<K, V>>

    companion object {
        fun <K, V> lru(capacity: Int = 5): Cache<K, V> = LRUCache(capacity)
    }
}