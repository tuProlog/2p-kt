package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.synchronizedOnSelf

internal class LRUCache<K, V>(
    override val capacity: Int,
) : Cache<K, V> {
    init {
        require(capacity > 0)
    }

    private val cache = mutableMapOf<K, V>()
    private val insertionOrder = (0 until capacity).map { Optional.none<K>() }.toTypedArray()

    private var nextFreeIndex = 0

    override val size
        get() = synchronizedOnSelf { cache.size }

    override fun set(
        key: K,
        value: V,
    ): Optional<out Pair<K, V>> =
        synchronizedOnSelf {
            val evicted: Optional<out Pair<K, V>> =
                insertionOrder[nextFreeIndex].let { evictedKey ->
                    if (evictedKey.isPresent) {
                        val evictedKeyValue = evictedKey.value!!
                        val evictedValue = cache[evictedKeyValue]!!
                        cache.remove(evictedKeyValue)
                        Optional.some(evictedKeyValue to evictedValue)
                    } else {
                        Optional.none()
                    }
                }
            insertionOrder[nextFreeIndex] = Optional.some(key)
            cache[key] = value
            nextFreeIndex = (nextFreeIndex + 1) % capacity
            evicted
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

        other as LRUCache<*, *>

        if (capacity != other.capacity) return false
        if (cache != other.cache) return false
        if (!insertionOrder.contentEquals(other.insertionOrder)) return false
        return nextFreeIndex == other.nextFreeIndex
    }

    override fun hashCode(): Int {
        var result = capacity
        result = 31 * result + cache.hashCode()
        result = 31 * result + insertionOrder.contentHashCode()
        result = 31 * result + nextFreeIndex
        return result
    }

    override fun toSequence(): Sequence<Pair<K, V>> =
        synchronizedOnSelf {
            val indexes =
                if (size < capacity) {
                    (0 until size).asSequence()
                } else {
                    (0 until capacity).asSequence().map { (it + nextFreeIndex) % capacity }
                }
            indexes
                .map { insertionOrder[it] }
                .filter { it.isPresent }
                .map { it.value!! }
                .map { it to cache[it]!! }
                .buffered()
        }

    override fun toString(): String =
        "LRUCache(${toSequence().map { "${it.first} = ${it.second}" }.joinToString(", ")})"
}
