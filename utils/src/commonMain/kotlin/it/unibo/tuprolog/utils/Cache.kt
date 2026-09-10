package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.LRUCache
import it.unibo.tuprolog.utils.impl.SimpleLRUCache

/**
 * Mutable, fixed-capacity cache whose eviction strategy depends on the specific implementation.
 *
 * A [Cache] is useful whenever some expensive computation (e.g. an MGU computation, a number-format
 * conversion, a parsed representation) is repeatedly requested for the same input, and memory usage must
 * be bounded: unlike a plain [MutableMap], a [Cache] never grows past [capacity], evicting older entries
 * (following the policy of the concrete implementation) as new ones are stored. Instances are created via
 * the factory methods in the [companion object][Cache.Companion], e.g. [Cache.lru] or [Cache.simpleLru].
 *
 * For instance, `it.unibo.tuprolog.unify.CachedUnificator` (in the `:unify` module) uses a [Cache] to
 * memoize the results of most general unifier computations:
 * ```kotlin
 * private val mguCache: Cache<MguRequest, Substitution> = Cache.simpleLru(cacheCapacity)
 * ```
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
    operator fun set(
        key: K,
        value: V,
    ): Optional<out Pair<K, V>>

    /**
     * Retrieves the cached value corresponding to the provided [key]
     * @param key is the key used for indexing the pair
     * @return the value corresponding to [key], if any
     */
    operator fun get(key: K): Optional<out V>

    /**
     * Retrieves the cached value corresponding to the provided [key], or stores a cache for the key in case it is missing
     * @param key is the key used for indexing the pair
     * @param valueGenerator is the function aimed at generating the value to be cached
     * @return the value corresponding to [key], if any, or the value produced by [valueGenerator] otherwise
     */
    fun getOrSet(
        key: K,
        valueGenerator: () -> V,
    ): V =
        when (val got = get(key)) {
            is Optional.Some -> got.value
            else -> {
                valueGenerator().also { set(key, it) }
            }
        }

    /**
     * Converts this cache to an immutable map, containing a snapshot of all the key-value pairs currently cached
     */
    fun toMap(): Map<K, V>

    /**
     * Converts this cache to a sequence of key-value pairs, containing a snapshot of all the key-value pairs
     * currently cached
     */
    fun toSequence(): Sequence<Pair<K, V>>

    companion object {
        /**
         * Creates a new LRU (least recently used) cache of the given [capacity]: whenever a new key-value pair
         * would exceed [capacity], the least recently inserted pair is evicted to make room for it.
         * @throws IllegalArgumentException if [capacity] is not strictly positive
         */
        fun <K, V> lru(capacity: Int = 5): Cache<K, V> = LRUCache(capacity)

        /**
         * Creates a new LRU (least recently used) cache of the given [capacity], using a simpler
         * (less memory-consuming, but functionally equivalent) implementation than the one returned by [lru].
         * @throws IllegalArgumentException if [capacity] is not strictly positive
         */
        fun <K, V> simpleLru(capacity: Int = 5): Cache<K, V> = SimpleLRUCache(capacity)
    }
}
