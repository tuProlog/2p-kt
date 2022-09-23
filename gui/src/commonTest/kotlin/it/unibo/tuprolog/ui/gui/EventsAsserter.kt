package it.unibo.tuprolog.ui.gui

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EventsAsserter<T>(events: Iterable<T>) {
    private val iterator: Iterator<T> = events.iterator()

    private fun <R> focussingOnNext(
        onError: () -> R = { throw AssertionError("No more events") },
        onItem: (T) -> R
    ) = if (iterator.hasNext()) {
        iterator.next().let(onItem)
    } else {
        onError()
    }

    fun assertLast(message: String? = null, predicate: (T) -> Boolean) {
        assertThereAreEvents()
        var last: T? = null
        while (iterator.hasNext()) {
            last = iterator.next()
        }
        assertTrue(predicate(last!!), message)
    }

    fun assertAny(message: String? = null, predicate: (T) -> Boolean) {
        while (iterator.hasNext()) {
            if (predicate(iterator.next())) {
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
        assertTrue(iterator.hasNext(), message)
    }

    fun assertNoMoreEvents(message: String? = null) {
        assertFalse(iterator.hasNext(), message)
    }
}

fun <T> Iterable<T>.assertions(debug: Boolean = false, scope: EventsAsserter<T>.() -> Unit) {
    if (debug) {
        val events = if (this.none()) "<none>" else joinToString("\n#\t- ") {
            it.toString().replace("\n", "\\n")
        }
        println("# Events:\n#\t- $events")
    }
    EventsAsserter(this).run(scope)
}
