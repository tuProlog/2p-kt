package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional
import it.unibo.tuprolog.utils.buffered
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile

internal class LRUCache<K, V>(override val capacity: Int) : Cache<K, V> {

    init {
        require(capacity > 0)
    }

    private val cache = mutableMapOf<K, V>()
    private val insertionOrder = (0 until capacity).map { Optional.none<K>() }.toTypedArray()
    @Volatile
    private var nextFreeIndex = 0

    override val size
        @Synchronized
        get() = cache.size

    @Synchronized
    override fun set(key: K, value: V): Optional<out Pair<K, V>> {
        val evicted: Optional<out Pair<K, V>> = insertionOrder[nextFreeIndex].let { evictedKey ->
            if (evictedKey is Optional.Some) {
                val evictedValue = cache[evictedKey.value]!!
                cache.remove(evictedKey.value)
                Optional.some(evictedKey.value to evictedValue)
            } else {
                Optional.none()
            }
        }
        insertionOrder[nextFreeIndex] = Optional.some(key)
        cache[key] = value
        nextFreeIndex = (nextFreeIndex + 1) % capacity
        return evicted
    }

    @Synchronized
    override fun get(key: K): Optional<out V> =
        if (cache.containsKey(key)) {
            Optional.of(cache[key])
        } else {
            Optional.none()
        }

    @Synchronized
    override fun toMap(): Map<K, V> =
        cache.toMap()

    @Synchronized
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LRUCache<*, *>

        if (capacity != other.capacity) return false
        if (cache != other.cache) return false
        if (!insertionOrder.contentEquals(other.insertionOrder)) return false
        if (nextFreeIndex != other.nextFreeIndex) return false

        return true
    }

    @Synchronized
    override fun hashCode(): Int {
        var result = capacity
        result = 31 * result + cache.hashCode()
        result = 31 * result + insertionOrder.contentHashCode()
        result = 31 * result + nextFreeIndex
        return result
    }

    @Synchronized
    override fun toSequence(): Sequence<Pair<K, V>> {
        val indexes = if (size < capacity) {
            (0 until size).asSequence()
        } else {
            (0 until capacity).asSequence()
                .map { (it + nextFreeIndex) % capacity }
        }
        return indexes.map { insertionOrder[it] }
            .filterIsInstance<Optional.Some<K>>()
            .map { it.value }
            .map { it to cache[it]!! }
            .buffered()
    }

    @Synchronized
    override fun toString(): String {
        return "LRUCache(${toSequence().map { "${it.first} = ${it.second}" }.joinToString(", ")})"
    }

    //    private fun getKeyToEvict(): K? =
//        insertionOrder[(nextFreeIndex + size) % capacity].value
//
//    private fun pop(): K {
//        val leastRecentIndex = (nextFreeIndex + capacity + 1) % capacity
//        val result = insertionOrder[leastRecentIndex]
//        insertionOrder[leastRecentIndex] = Optional.empty()
//        cache.remove(result)
//    }
}