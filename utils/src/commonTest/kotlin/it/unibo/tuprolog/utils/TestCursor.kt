package it.unibo.tuprolog.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TestCursor {
    private fun <T> testCursor(cursor: Cursor<out T>) {
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

    private fun <T> assertCursorContains(
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

    private val items = 1..10

    @Test
    fun testEmptyCursor() {
        val cursor = Cursor.empty<Int>()
        testCursor(cursor)
        assertCursorContains(cursor, emptyList())
    }

    @Test
    fun testCursorAttainedFromList() {
        val cursor = items.toList().cursor()
        testCursor(cursor)
        assertCursorContains(cursor, items)
    }

    @Test
    fun testCursorAttainedFromIterable() {
        val cursor = items.cursor()
        testCursor(cursor)
        assertCursorContains(cursor, items)
    }

    @Test
    fun testCursorAttainedFromSequence() {
        val cursor = items.asSequence().cursor()
        testCursor(cursor)
        assertCursorContains(cursor, items)
    }

    @Test
    fun testCursorAttainedFromCollection() {
        val cursor = (items.toList() as Collection<Int>).cursor()
        testCursor(cursor)
        assertCursorContains(cursor, items)
    }

    @Test
    fun testCursorAttainedFromMap() {
        val cursor = items.cursor().map { it * 2 }
        testCursor(cursor)
        assertCursorContains(cursor, items.map { it * 2 })
    }

    @Test
    fun testCursorAttainedFromPlus() {
        val cursor = items.cursor() + items.cursor()
        testCursor(cursor)
        assertCursorContains(cursor, items.toList() + items.toList())
    }

    @Test
    fun testCursorDoNotRepeatSequence() {
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

    @Test
    fun stressMapping() {
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
}
