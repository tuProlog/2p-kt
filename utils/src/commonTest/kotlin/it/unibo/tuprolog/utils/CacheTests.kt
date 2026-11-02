package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.funSpec
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private val ITEMS: List<Pair<String, Int>> =
    (0 until 26)
        .asSequence()
        .map { charArrayOf('a' + it).concatToString() to it }
        .toList()

private const val DEFAULT_CAPACITY = 5

fun cacheTests(cacheFactory: (Int) -> Cache<String, Int>) =
    funSpec {
        test("keys-caching") {
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

        test("contents/keep-the-most-recent-items-in-insertion-order") {
            val cache = cacheFactory(DEFAULT_CAPACITY)
            ITEMS.take(DEFAULT_CAPACITY + 2).forEach { (key, value) -> cache[key] = value }
            val expected = ITEMS.drop(2).take(DEFAULT_CAPACITY)
            assertEquals(expected, cache.toSequence().toList())
            assertEquals(expected.toMap(), cache.toMap())
            assertTrue(cache.toString().contains("${expected.first().first} = ${expected.first().second}"))
        }

        test("contents/before-filling-up") {
            val cache = cacheFactory(DEFAULT_CAPACITY)
            ITEMS.take(2).forEach { (key, value) -> cache[key] = value }
            assertEquals(ITEMS.take(2), cache.toSequence().toList())
        }

        test("equality/caches-fed-the-same-items-are-equal") {
            fun filled() = cacheFactory(DEFAULT_CAPACITY).also { cache -> ITEMS.forEach { (k, v) -> cache[k] = v } }
            assertEquals(filled(), filled())
            assertEquals(filled().hashCode(), filled().hashCode())
            assertNotEquals(filled(), cacheFactory(DEFAULT_CAPACITY))
        }

        test("get-or-set/computes-only-missing-values") {
            val cache = cacheFactory(DEFAULT_CAPACITY)
            assertEquals(1, cache.getOrSet("a") { 1 })
            assertEquals(1, cache.getOrSet("a") { 2 })
        }

        test("capacity/must-be-positive") {
            assertFailsWith<IllegalArgumentException> { cacheFactory(0) }
        }
    }
