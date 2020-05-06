package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.LRUCache
import it.unibo.tuprolog.utils.impl.SimpleLRUCache

/**
 * Mutable, fixed-capacity cache whose eviction strategy depends on the specific implementation
 * @param K is the type of the keys used for indexing items in this cache
 * @param V is the type of the values stored in this cache
 */
interface Cache<K, V> {

    /**
     * Retrieves the maximum amount of items this cache may ever store
     */
    val capacity: Int

    /**
     * Retrieves the amount of items currently cached by this cache
     */
    val size: Int

    /**
     * Stores a new key-value pair in this cache, possibly evicting some previously stored key-value pair
     * @param key is the key used for indexing the pair
     * @param value is the value corresponding to [key]
     * @return the evicted key-value pair, if any
     */
    operator fun set(key: K, value: V): Optional<out Pair<K, V>>

    /**
     * Retrieves the cached value corresponding to the provided [key]
     * @param key is the key used for indexing the pair
     * @return the value corresponding to [key], if any
     */
    operator fun get(key: K): Optional<out V>

    /**
     * Converts this cache to an immutable map
     */
    fun toMap(): Map<K, V>

    /**
     * Converts this cache to a sequenve of key-value pairs
     */
    fun toSequence(): Sequence<Pair<K, V>>

    companion object {
        /**
         * Creates a new LRU (least recently used) cache
         */
        fun <K, V> lru(capacity: Int = 5): Cache<K, V> = LRUCache(capacity)

        /**
         * Creates a new LRU (least recently used) cache, using a simpler (less memory-consuming) implementation
         */
        fun <K, V> simpleLru(capacity: Int = 5): Cache<K, V> = SimpleLRUCache(capacity)
    }
}