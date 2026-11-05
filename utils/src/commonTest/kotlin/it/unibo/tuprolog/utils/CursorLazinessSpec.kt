package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

class CursorLazinessSpec :
    FunSpec({
        fun <T> Cursor<out T>.take(n: Int): List<T> = asSequence().take(n).toList()

        test("map/lazy-on-infinite-source") {
            val mapped = generateSequence(0) { it + 1 }.cursor().map { it * 2 }.map { it + 1 }
            mapped.take(3) shouldBe listOf(1, 3, 5)
        }

        test("map/counted-source-prefix") {
            var produced = 0
            val source =
                sequence {
                    for (i in 0 until 1_000) {
                        produced++
                        yield(i)
                    }
                }
            val mapped = source.cursor().map { it + 1 }.map { it * 10 }
            mapped.take(3) shouldBe listOf(10, 20, 30)
            produced shouldBeLessThanOrEqual 4
        }

        test("map/throwing-suffix") {
            val source =
                sequence {
                    yield(1)
                    yield(2)
                    error("suffix evaluated")
                }
            val mapped = source.cursor().map { it + 1 }.map { it + 1 }
            mapped.current shouldBe 3
            mapped.next.current shouldBe 4
        }

        test("map/memoized-current-and-next") {
            var calls = 0
            val mapped = listOf(1, 2, 3).cursor().map { calls++; it }
            repeat(3) { mapped.next.current }
            repeat(3) { mapped.next.next.current }
            calls shouldBe 2
        }

        test("map/composed-100k-maps") {
            var cursor: Cursor<out Int> = generateSequence(0) { it + 1 }.cursor()
            repeat(100_000) { cursor = cursor.map { it + 1 } }
            cursor.current shouldBe 100_000
            cursor.next.current shouldBe 100_001
        }
    })
