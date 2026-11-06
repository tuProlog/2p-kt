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
            val mapped =
                listOf(1, 2, 3).cursor().map {
                    calls++
                    it
                }
            repeat(3) { mapped.next.current }
            repeat(3) { mapped.next.next.current }
            calls shouldBe 2
        }

        test("map/composition-reuses-intermediate-results") {
            var calls = 0
            val mapped = listOf(1, 2, 3).cursor().map { ++calls }
            val branch = mapped.map { it * 10 }
            mapped.current shouldBe 1
            branch.current shouldBe 10
            mapped.next.current shouldBe 2
            branch.next.current shouldBe 20
            mapped.map { it * 100 }.next.current shouldBe 200
            calls shouldBe 2
        }

        test("map/composition-preserves-null-results") {
            var calls = 0
            val mapped =
                listOf(1, 2).cursor().map<Int?> {
                    calls++
                    null
                }
            mapped.current shouldBe null
            mapped.map { it == null }.current shouldBe true
            calls shouldBe 1
        }

        test("map/equal-by-source-and-mappers") {
            val source = listOf(1, 2, 3).cursor()
            val inc: (Int) -> Int = { it + 1 }
            val double: (Int) -> Int = { it * 2 }
            (source.map(inc).map(double) == source.map(inc).map(double)) shouldBe true
            source.map(inc).map(double).hashCode() shouldBe source.map(inc).map(double).hashCode()
            (source.map(inc).map(double) == source.map(double).map(inc)) shouldBe false
            (source.map(inc) == source.map(inc).map(double)) shouldBe false
        }

        test("map/composed-100k-maps") {
            var cursor: Cursor<out Int> = generateSequence(0) { it + 1 }.cursor()
            repeat(100_000) { cursor = cursor.map { it + 1 } }
            cursor.current shouldBe 100_000
            cursor.next.current shouldBe 100_001
        }
    })
