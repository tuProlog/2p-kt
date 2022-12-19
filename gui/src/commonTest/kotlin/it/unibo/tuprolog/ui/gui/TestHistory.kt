package it.unibo.tuprolog.ui.gui

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class TestHistory {
    private lateinit var history1: History<String>
    private lateinit var history2: History<String>
    private lateinit var events: MutableList<Event<Any>>

    @BeforeTest
    fun setup() {
        events = mutableListOf()
        history1 = History.empty()
        history2 = History.of("a", "b", "c")
        for (history in listOf(history1, history2)) {
            history.run {
                val catchAnyEvent: (Event<Any>) -> Unit = { events.add(it) }
                onSelected += catchAnyEvent
                onAppended += catchAnyEvent
            }
        }
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @Test
    fun testInitialization() {
        assertEquals(emptyList(), history1.items)
        assertEquals(listOf("a", "b", "c"), history2.items)
        assertFailsWith<IllegalStateException> { history1.selectedIndex }
        assertEquals(0, history2.selectedIndex)
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @Test
    fun testAppendToEmpty() {
        history1.append("a")
        assertEquals(listOf("a"), history1.items)
        assertEquals(0, history1.selectedIndex)
        events.assertions {
            assertNextEquals(Event.of(History.EVENT_APPENDED, "a"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "a"))
            assertNoMoreEvents()
        }
    }

    @Test
    fun testAppendToNonEmpty() {
        history2.append("d")
        assertEquals(listOf("d", "a", "b", "c"), history2.items)
        assertEquals(0, history2.selectedIndex)
        events.assertions {
            assertNextEquals(Event.of(History.EVENT_APPENDED, "d"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "d"))
            assertNoMoreEvents()
        }
    }

    @Test
    fun testSelectIncreasing() {
        history2.selectedIndex++
        assertEquals(1, history2.selectedIndex)
        history2.selectedIndex++
        assertEquals(2, history2.selectedIndex)
        history2.selectedIndex++
        assertEquals(0, history2.selectedIndex)
        events.assertions {
            assertNextEquals(Event.of(History.EVENT_SELECTED, 1 to "b"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 2 to "c"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "a"))
            assertNoMoreEvents()
        }
    }

    @Test
    fun testSelectDecreasing() {
        history2.selectedIndex--
        assertEquals(2, history2.selectedIndex)
        history2.selectedIndex--
        assertEquals(1, history2.selectedIndex)
        history2.selectedIndex--
        assertEquals(0, history2.selectedIndex)
        events.assertions {
            assertNextEquals(Event.of(History.EVENT_SELECTED, 2 to "c"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 1 to "b"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "a"))
            assertNoMoreEvents()
        }
    }

    @Test
    fun testSelectSpecific() {
        history2.selected = "c"
        assertEquals(2, history2.selectedIndex)
        events.assertions {
            assertNextEquals(Event.of(History.EVENT_SELECTED, 2 to "c"))
            assertNoMoreEvents()
        }
    }

    @Test
    fun testSelectMissing() {
        assertFailsWith<NoSuchElementException> { history2.selected = "d" }
        assertEquals(0, history2.selectedIndex)
        events.assertions {
            assertNoMoreEvents()
        }
    }

    @Test
    fun testEquality() {
        assertNotEquals(history2, history1)
        history1.append("c")
        assertNotEquals(history2, history1)
        history1.append("b")
        assertNotEquals(history2, history1)
        history1.append("a")
        assertEquals(history2, history1)
        events.assertions {
            assertNextEquals(Event.of(History.EVENT_APPENDED, "c"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "c"))
            assertNextEquals(Event.of(History.EVENT_APPENDED, "b"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "b"))
            assertNextEquals(Event.of(History.EVENT_APPENDED, "a"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "a"))
            assertNoMoreEvents()
        }
    }

    @Test
    fun testAppendResetsSelection() {
        history2.selectedIndex = 2
        assertEquals(2, history2.selectedIndex)
        history2.append("d")
        assertEquals(0, history2.selectedIndex)
        events.assertions {
            assertNextEquals(Event.of(History.EVENT_SELECTED, 2 to "c"))
            assertNextEquals(Event.of(History.EVENT_APPENDED, "d"))
            assertNextEquals(Event.of(History.EVENT_SELECTED, 0 to "d"))
            assertNoMoreEvents()
        }
    }
}
