package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CursorSpec :
    FunSpec({
        fun <T> testCursor(cursor: Cursor<out T>) {
            var current: Cursor<out T> = cursor
            while (!current.isOver) {
                assertTrue { current.hasNext }
                assertFalse { current.isOver }
                assertNotNull(current.current)
                assertSame(current.current, current.current)
                assertTrue {
                    @Suppress("USELESS_IS_CHECK")
                    current.next is Cursor<out T>
                }
                println(current.current)
                current = current.next
            }
            assertFalse { current.hasNext }
            assertTrue { current.isOver }
            assertNull(current.current)
            assertSame(current.current, current.current)
            assertFailsWith(NoSuchElementException::class) {
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
                assertTrue("The cursor has only $i items, while more are expected") {
                    iterActual.hasNext()
                }
                assertEquals(
                    iterExpected.next(),
                    iterActual.next(),
                    "Items with index ${i - 1} does not correspond",
                )
                i++
            }
            assertEquals(
                iterExpected.hasNext(),
                iterActual.hasNext(),
                "The cursor has more than $i items, while $i are expected",
            )
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
            assertEquals(items.count(), x)
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
                assertTrue { x <= items.count() * (i + 1) }
            }
            testCursor(cursor)
            assertCursorContains(cursor, items.map { it + n })
            assertEquals(items.count() * n, x)
        }
    })
