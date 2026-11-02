package it.unibo.tuprolog.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class CursorSpec :
    FunSpec({
        fun <T> testCursor(cursor: Cursor<out T>) {
            var current: Cursor<out T> = cursor
            while (!current.isOver) {
                current.hasNext.shouldBeTrue()
                current.isOver.shouldBeFalse()
                current.current.shouldNotBeNull()
                current.current shouldBeSameInstanceAs current.current
                (
                    run {
                        @Suppress("USELESS_IS_CHECK")
                        current.next is Cursor<out T>
                    }
                ).shouldBeTrue()
                println(current.current)
                current = current.next
            }
            current.hasNext.shouldBeFalse()
            current.isOver.shouldBeTrue()
            current.current.shouldBeNull()
            current.current shouldBeSameInstanceAs current.current
            shouldThrow<NoSuchElementException> {
                current.next
            }
        }

        fun <T> assertCursorContains(
            actual: Cursor<out T>,
            expected: Iterable<T>,
        ) {
            var i = 1
            val iterActual = actual.iterator()
            val iterExpected = expected.iterator()
            while (iterExpected.hasNext()) {
                withClue(
                    "The cursor has only $i items, while more are expected",
                ) { iterActual.hasNext().shouldBeTrue() }
                withClue("Items with index ${i - 1} does not correspond") {
                    iterActual.next() shouldBe
                        iterExpected.next()
                }
                i++
            }
            withClue("The cursor has more than $i items, while $i are expected") {
                iterActual.hasNext() shouldBe
                    iterExpected.hasNext()
            }
        }

        val items = 1..10

        test("empty-cursor") {
            val cursor = Cursor.empty<Int>()
            testCursor(cursor)
            assertCursorContains(cursor, emptyList())
        }

        test("cursor-attained-from-list") {
            val cursor = items.toList().cursor()
            testCursor(cursor)
            assertCursorContains(cursor, items)
        }

        test("cursor-attained-from-iterable") {
            val cursor = items.cursor()
            testCursor(cursor)
            assertCursorContains(cursor, items)
        }

        test("cursor-attained-from-sequence") {
            val cursor = items.asSequence().cursor()
            testCursor(cursor)
            assertCursorContains(cursor, items)
        }

        test("cursor-attained-from-collection") {
            val cursor = (items.toList() as Collection<Int>).cursor()
            testCursor(cursor)
            assertCursorContains(cursor, items)
        }

        test("cursor-attained-from-map") {
            val cursor = items.cursor().map { it * 2 }
            testCursor(cursor)
            assertCursorContains(cursor, items.map { it * 2 })
        }

        test("cursor-attained-from-plus") {
            val cursor = items.cursor() + items.cursor()
            testCursor(cursor)
            assertCursorContains(cursor, items.toList() + items.toList())
        }

        test("cursor-do-not-repeat-sequence") {
            var x: Int = 0
            val cursor =
                items
                    .asSequence()
                    .map {
                        x++
                        x
                    }.cursor()
                    .map { it * 2 }
            testCursor(cursor)
            assertCursorContains(cursor, items.map { it * 2 })
            x shouldBe items.count()
        }

        test("stress-mapping") {
            val n = 100000
            var x: Int = 0
            var cursor = items.cursor()
            for (i in 0 until n) {
                cursor =
                    cursor.map {
                        x++
                        it + 1
                    }
                (x <= items.count() * (i + 1)).shouldBeTrue()
            }
            testCursor(cursor)
            assertCursorContains(cursor, items.map { it + n })
            x shouldBe (items.count() * n)
        }
    })
