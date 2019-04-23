package it.unibo.tuprolog.theory

internal interface MapList<K, V> : Map<K, V> {

    companion object {

        fun <K, V> of(key: K, value: V): MapList<K, V> {
            return of(Pair(key, value))
        }

        fun <K, V> of(other: Map<K, V>): MapList<K, V> {
            return LinkedHashMalList(LinkedHashMap(other))
        }

        fun <K, V> of(vararg pairs: Pair<K, V>): MapList<K, V> {
            return LinkedHashMalList(LinkedHashMap<K, V>().apply {
                putAll(pairs)
            })
        }

        fun <K, V> of(pairs: Sequence<Pair<K, V>>): MapList<K, V> {
            return LinkedHashMalList(LinkedHashMap<K, V>().apply {
                putAll(pairs)
            })
        }

        fun <K, V> of(pairs: Iterable<Pair<K, V>>): MapList<K, V> {
            return LinkedHashMalList(LinkedHashMap<K, V>().apply {
                putAll(pairs)
            })
        }

//        fun <K, V> of(vararg pairs: Map.Entry<K, V>): MapList<K, V> {
//            return LinkedHashMalList(LinkedHashMap<K, V>().apply {
//                pairs.forEach { put(it.key, it.value) }
//            })
//        }

//        fun <K, V> of(pairs: Sequence<Map.Entry<K, V>>): MapList<K, V> {
//            return LinkedHashMalList(LinkedHashMap<K, V>().apply {
//                pairs.forEach { put(it.key, it.value) }
//            })
//        }
//
//        fun <K, V> of(pairs: Iterable<Map.Entry<K, V>>): MapList<K, V> {
//            return LinkedHashMalList(LinkedHashMap<K, V>().apply {
//                pairs.forEach { put(it.key, it.value) }
//            })
//        }
    }

    fun toSequence(): Sequence<Pair<K, V>> {
        return asSequence().map { it.toPair() }
    }

    operator fun plus(other: MapList<K, V>): MapList<K, V> {
        return MapList.of(this.asSequence().map { it.toPair() } + other.asSequence().map { it.toPair() })
    }
}

internal operator fun <K, V> Map<K, V>.plus(other: MapList<K, V>): MapList<K, V> {
    return MapList.of(this.asSequence().map { it.toPair() } + other.asSequence().map { it.toPair() })
}

private class LinkedHashMalList<K, V>(val wrapped: LinkedHashMap<K, V>) : MapList<K, V> {
    override val entries: Set<Map.Entry<K, V>>
        get() = wrapped.entries

    override val keys: Set<K>
        get() = wrapped.keys

    override val size: Int
        get() = wrapped.size

    override val values: Collection<V>
        get() = wrapped.values

    override fun containsKey(key: K): Boolean {
        return wrapped.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return wrapped.containsValue(value)
    }

    override fun get(key: K): V? {
        return wrapped.get(key)
    }

    override fun isEmpty(): Boolean {
        return wrapped.isEmpty()
    }
}