package it.unibo.tuprolog.utils

import kotlin.test.assertEquals

interface CacheTest {
    companion object {
        fun prototype(cacheConstructor: (Int) -> Cache<String, Int>): CacheTest =
            object : CacheTest {
                override fun cacheFactory(capacity: Int): Cache<String, Int> = cacheConstructor(capacity)
            }

        private val ITEMS: List<Pair<String, Int>> =
            (0 until 26)
                .asSequence()
                .map { charArrayOf('a' + it).concatToString() to it }
                .toList()

        private const val DEFAULT_CAPACITY = 5
    }

    fun cacheFactory(capacity: Int): Cache<String, Int>

    fun testKeysCaching() {
        val cache = cacheFactory(DEFAULT_CAPACITY)

        var i = 0

        while (i < DEFAULT_CAPACITY) {
            assertEquals(i, cache.size)
            assertEquals(Optional.none(), cache.set(ITEMS[i].first, ITEMS[i].second))
            i++
            for (j in 0 until i) {
                assertEquals(Optional.some(ITEMS[j].second), cache[ITEMS[j].first])
            }
            for (j in i until ITEMS.size) {
                assertEquals(Optional.none(), cache[ITEMS[j].first])
            }
        }

        while (i < ITEMS.size) {
            assertEquals(DEFAULT_CAPACITY, cache.size)
            assertEquals(Optional.some(ITEMS[i - DEFAULT_CAPACITY]), cache.set(ITEMS[i].first, ITEMS[i].second))
            i++
            for (j in 0 until (i - DEFAULT_CAPACITY)) {
                assertEquals(Optional.none(), cache[ITEMS[j].first])
            }
            for (j in (i - DEFAULT_CAPACITY) until i) {
                assertEquals(Optional.some(ITEMS[j].second), cache[ITEMS[j].first])
            }
            for (j in i until ITEMS.size) {
                assertEquals(Optional.none(), cache[ITEMS[j].first])
            }
        }
    }
}
