package it.unibo.tuprolog.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

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
                cache.size shouldBe i
                cache.set(ITEMS[i].first, ITEMS[i].second) shouldBe Optional.none()
                i++
                for (j in 0 until i) {
                    cache[ITEMS[j].first] shouldBe Optional.some(ITEMS[j].second)
                }
                for (j in i until ITEMS.size) {
                    cache[ITEMS[j].first] shouldBe Optional.none()
                }
            }

            while (i < ITEMS.size) {
                cache.size shouldBe DEFAULT_CAPACITY
                cache.set(ITEMS[i].first, ITEMS[i].second) shouldBe Optional.some(ITEMS[i - DEFAULT_CAPACITY])
                i++
                for (j in 0 until (i - DEFAULT_CAPACITY)) {
                    cache[ITEMS[j].first] shouldBe Optional.none()
                }
                for (j in (i - DEFAULT_CAPACITY) until i) {
                    cache[ITEMS[j].first] shouldBe Optional.some(ITEMS[j].second)
                }
                for (j in i until ITEMS.size) {
                    cache[ITEMS[j].first] shouldBe Optional.none()
                }
            }
        }

        test("contents/keep-the-most-recent-items-in-insertion-order") {
            val cache = cacheFactory(DEFAULT_CAPACITY)
            ITEMS.take(DEFAULT_CAPACITY + 2).forEach { (key, value) -> cache[key] = value }
            val expected = ITEMS.drop(2).take(DEFAULT_CAPACITY)
            cache.toSequence().toList() shouldBe expected
            cache.toMap() shouldBe expected.toMap()
            cache.toString().contains("${expected.first().first} = ${expected.first().second}").shouldBeTrue()
        }

        test("contents/before-filling-up") {
            val cache = cacheFactory(DEFAULT_CAPACITY)
            ITEMS.take(2).forEach { (key, value) -> cache[key] = value }
            cache.toSequence().toList() shouldBe ITEMS.take(2)
        }

        test("equality/caches-fed-the-same-items-are-equal") {
            fun filled() = cacheFactory(DEFAULT_CAPACITY).also { cache -> ITEMS.forEach { (k, v) -> cache[k] = v } }
            filled() shouldBe filled()
            filled().hashCode() shouldBe filled().hashCode()
            cacheFactory(DEFAULT_CAPACITY) shouldNotBe filled()
        }

        test("get-or-set/computes-only-missing-values") {
            val cache = cacheFactory(DEFAULT_CAPACITY)
            (cache.getOrSet("a") { 1 }) shouldBe 1
            (cache.getOrSet("a") { 2 }) shouldBe 1
        }

        test("capacity/must-be-positive") {
            shouldThrow<IllegalArgumentException> { cacheFactory(0) }
        }
    }
