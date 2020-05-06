package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional

internal class SimpleLRUCache<K, V>(override val capacity: Int) : Cache<K, V> {

    init {
        require(capacity > 0)
    }

    private val cache = LinkedHashMap<K, V>()

    override fun set(key: K, value: V): Optional<out Pair<K, V>> {
        val evicted = removeLeastRecentIfNecessary()
        cache[key] = value
        return evicted
    }

    private fun removeLeastRecent(): Optional<out Pair<K, V>> {
        val entry = cache.iterator().next()
        cache.remove(entry.key)
        return Optional.some(entry.toPair())
    }

    private fun removeLeastRecentIfNecessary(): Optional<out Pair<K, V>> {
        return if (cache.size >= capacity) {
            removeLeastRecent()
        } else {
            return Optional.none()
        }
    }

    override fun get(key: K): Optional<out V> =
        if (cache.containsKey(key)) {
            Optional.of(cache[key])
        } else {
            Optional.none()
        }

    override fun toMap(): Map<K, V> =
        cache.toMap()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SimpleLRUCache<*, *>

        if (capacity != other.capacity) return false
        if (cache != other.cache) return false

        return true
    }

    override fun hashCode(): Int {
        var result = capacity
        result = 31 * result + cache.hashCode()
        return result
    }

    override fun toSequence(): Sequence<Pair<K, V>> {
        return cache.entries.asSequence().map { it.toPair() }
    }

    override fun toString(): String {
        return "SimpleLRUCache(${toSequence().map { "${it.first} = ${it.second}" }.joinToString(", ")})"
    }

    override val size: Int
        get() = cache.size
}