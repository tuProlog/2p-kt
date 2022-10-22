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
        val events = if (events.none()) {
            "<none>"
        } else {
            val indexes = generateSequence(0, Int::inc)
            events.asSequence().zip(indexes).joinToString("\n#\t") { (it, i) ->
                i.toString() + ") " + it.toString().replace("\n", "\\n")
            }
        }
        println("# Events:\n#\t$events")
    }

    private fun <R> focussingOnNext(
        onError: () -> R = { throw AssertionError("No more events") },
        onItem: (T) -> R
    ) = if (hasNext()) {
        next().let(onItem)
    } else {
        onError()
    }

    fun assertLast(
        message: String? = "Assertion failed for last event",
        predicate: (T) -> Boolean
    ) {
        assertThereAreEvents()
        var last: T? = null
        while (hasNext()) {
            last = next()
        }
        assertTrue(predicate(last!!), message)
    }

    fun assertAny(message: String? = null, predicate: (T) -> Boolean) {
        val pivot = consumed
        while (hasNext()) {
            if (predicate(next())) {
                return
            }
        }
        throw AssertionError(message ?: "Starting from event $pivot, there are no more events matching predicate")
    }

    fun assertNext(
        message: String? = "Event $consumed is not matching predicate",
        predicate: (T) -> Boolean
    ) = focussingOnNext {
        assertTrue(predicate(it), message)
    }

    fun aboutNext(action: (T) -> Unit) = focussingOnNext {
        try {
            action(it)
        } catch (e: AssertionError) {
            throw AssertionError("Problem with event ${consumed - 1}: ${e.message}")
        }
    }

    fun assertNextEquals(
        expected: T,
        message: String? = "Event $consumed should be equals to $expected"
    ) = focussingOnNext {
        assertEquals(expected, it, message)
    }

    fun assertNextNotEquals(
        expected: T,
        message: String? = "Event $consumed should NOT be equals to $expected"
    ) = focussingOnNext {
        assertNotEquals(expected, it, message)
    }

    fun assertNextSame(
        expected: T,
        message: String? = "Event $consumed should be the same of $expected"
    ) = focussingOnNext {
        assertSame(expected, it, message)
    }

    fun assertNextNotSame(
        expected: T,
        message: String? = "Event $consumed should NOT be the same of $expected"
    ) = focussingOnNext {
        assertNotSame(expected, it, message)
    }

    fun assertNextNull(
        message: String? = "Event $consumed should be null"
    ) = focussingOnNext {
        assertNull(it, message)
    }

    fun assertNextNotNull(
        message: String? = "Event $consumed should be null"
    ) = focussingOnNext {
        assertNotNull(it, message)
    }

    fun assertThereAreEvents(
        message: String? = "There should be other events after event $consumed"
    ) {
        assertTrue(hasNext(), message)
    }

    fun assertNoMoreEvents(
        message: String? = "There shouldn't be any other event after event $consumed"
    ) {
        assertFalse(hasNext(), message)
    }
}
