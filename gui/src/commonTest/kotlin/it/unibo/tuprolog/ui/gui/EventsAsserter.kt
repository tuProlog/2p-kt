package it.unibo.tuprolog.ui.gui

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EventsAsserter<T>(private val events: Iterable<T>, private var consumed: Int = 0) {

    inner class Checkpoint(private val consumed: Int) {
        fun restore(): EventsAsserter<T> {
            return EventsAsserter(events, consumed)
        }
    }

    private val iterator: Iterator<T> = events.asSequence().drop(consumed).iterator()

    private fun hasNext(): Boolean = iterator.hasNext()

    private fun next(): T = iterator.next().also { consumed++ }

    fun checkpoint(): Checkpoint = Checkpoint(consumed)

    internal fun debug() {
        val events = if (events.none()) "<none>" else events.joinToString("\n#\t- ") {
            it.toString().replace("\n", "\\n")
        }
        println("# Events:\n#\t- $events")
    }

    private fun <R> focussingOnNext(
        onError: () -> R = { throw AssertionError("No more events") },
        onItem: (T) -> R
    ) = if (hasNext()) {
        next().let(onItem)
    } else {
        onError()
    }

    fun assertLast(message: String? = null, predicate: (T) -> Boolean) {
        assertThereAreEvents()
        var last: T? = null
        while (hasNext()) {
            last = next()
        }
        assertTrue(predicate(last!!), message)
    }

    fun assertAny(message: String? = null, predicate: (T) -> Boolean) {
        while (hasNext()) {
            if (predicate(next())) {
                return
            }
        }
        throw AssertionError(message)
    }

    fun assertNext(message: String? = null, predicate: (T) -> Boolean) = focussingOnNext {
        assertTrue(predicate(it), message)
    }

    fun aboutNext(action: (T) -> Unit) = focussingOnNext {
        action(it)
    }

    fun assertNextEquals(expected: T, message: String? = null) = focussingOnNext {
        assertEquals(expected, it, message)
    }

    fun assertNextNotEquals(expected: T, message: String? = null) = focussingOnNext {
        assertNotEquals(expected, it, message)
    }

    fun assertNextSame(expected: T, message: String? = null) = focussingOnNext {
        assertSame(expected, it, message)
    }

    fun assertNextNotSame(expected: T, message: String? = null) = focussingOnNext {
        assertNotSame(expected, it, message)
    }

    fun assertNextNull(message: String? = null) = focussingOnNext {
        assertNull(it, message)
    }

    fun assertNextNotNull(message: String? = null) = focussingOnNext {
        assertNotNull(it, message)
    }

    fun assertThereAreEvents(message: String? = null) {
        assertTrue(hasNext(), message)
    }

    fun assertNoMoreEvents(message: String? = null) {
        assertFalse(hasNext(), message)
    }
}
