package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cache
import it.unibo.tuprolog.utils.Optional

internal class LRUCache<K, V>(override val capacity: Int) : Cache<K, V> {

    init {
        require(capacity > 0)
    }

    private val cache = HashMap<K, V>()
    private val insertionOrder = (0 until capacity).map { Optional.empty<K>() }.toTypedArray()
    private val size get() = cache.size
    private var nextFreeIndex = 0

    override fun set(key: K, value: V): K? {
        val evicted = insertionOrder[nextFreeIndex].value?.also {
            cache.remove(it)
        }
        insertionOrder[nextFreeIndex] = Optional.of(key)
        cache[key] = value
        nextFreeIndex = (nextFreeIndex + 1) % capacity
        return evicted
    }

    override fun get(key: K): V? =
        cache[key]

    override fun toMap(): Map<K, V> =
        cache.toMap()

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

    override fun hashCode(): Int {
        var result = capacity
        result = 31 * result + cache.hashCode()
        result = 31 * result + insertionOrder.contentHashCode()
        result = 31 * result + nextFreeIndex
        return result
    }

    override fun toSequence(): Sequence<Pair<K, V>> {
        val lri = leastRecentIndex
        return (0 until size).asSequence()
            .map { (it + lri) % capacity }
            .map { insertionOrder[it] }
            .take(cache.size)
            .filterIsInstance<Optional.Some<K>>()
            .map { it.value }
            .map { it to cache[it]!! }
    }

    override fun toString(): String {
        return "LRUCache(${toSequence().map { "${it.first} = ${it.second}" }.joinToString(",")})"
    }


    private val leastRecentIndex: Int
        get() = (nextFreeIndex + capacity + 1) % capacity

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