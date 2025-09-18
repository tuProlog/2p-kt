package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.synchronizedOnSelf

internal class SimpleLRUCache<K, V>(
    override val capacity: Int,
) : Cache<K, V> {
    init {
        require(capacity > 0)
    }

    private val cache = LinkedHashMap<K, V>()

    override fun set(
        key: K,
        value: V,
    ): Optional<out Pair<K, V>> =
        synchronizedOnSelf {
            val evicted = removeLeastRecentIfNecessary()
            cache[key] = value
            evicted
        }

    private fun removeLeastRecent(): Optional<out Pair<K, V>> {
        val (key, value) = cache.asIterable().first()
        cache.remove(key)
        return Optional.some(key to value)
    }

    private fun removeLeastRecentIfNecessary(): Optional<out Pair<K, V>> =
        if (cache.size >= capacity) {
            removeLeastRecent()
        } else {
            Optional.none()
        }

    override fun get(key: K): Optional<out V> =
        synchronizedOnSelf {
            if (cache.containsKey(key)) {
                Optional.of(cache[key])
            } else {
                Optional.none()
            }
        }

    override fun toMap(): Map<K, V> =
        synchronizedOnSelf {
            cache.toMap()
        }

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

    override fun toSequence(): Sequence<Pair<K, V>> =
        synchronizedOnSelf {
            cache.entries
                .asSequence()
                .map { it.toPair() }
                .buffered()
        }

    override fun toString(): String =
        "SimpleLRUCache(${toSequence().map { "${it.first} = ${it.second}" }.joinToString(", ")})"

    override val size: Int
        get() = synchronizedOnSelf { cache.size }
}
