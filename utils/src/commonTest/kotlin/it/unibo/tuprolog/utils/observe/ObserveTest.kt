package it.unibo.tuprolog.utils.observe

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ObserveTest {
    @Test
    fun testInteraction() {
        val events = mutableListOf<Any>()
        val observer = Observer(events::add)
        val observable = Observable.of<Any>()
        val source = Source.of(observable)

        assertEquals(0, observable.observers.size)
        assertSame(source.observers, observable.observers)

        source.raise(1)

        observable += observer
        assertEquals(1, observable.observers.size)
        assertSame(source.observers, observable.observers)

        source.raise("a")

        observable -= observer
        assertEquals(0, observable.observers.size)
        assertSame(source.observers, observable.observers)

        source.raise(2)

        source.bind(observer)
        assertEquals(1, source.observers.size)
        assertSame(observable.observers, source.observers)

        source.raise("b")

        source.unbind(observer)
        assertEquals(0, source.observers.size)
        assertSame(observable.observers, source.observers)

        source.raise(3)

        source += observer
        val binding = source.bind(observer)
        assertEquals(1, source.observers.size)
        assertSame(observable.observers, source.observers)

        source.raise("c")

        binding.unbind()
        assertEquals(0, source.observers.size)
        assertSame(observable.observers, source.observers)

        source.raise(4)

        assertEquals(listOf<Any>("a", "b", "c"), events)
    }

    @Test
    fun testFilterAndMap() {
        val events = mutableListOf<String>()
        val observer = Observer(events::add)
        val observable = Observable.of<Int>()
        observable.filter { it % 2 == 0 }.map { it.toString() }.bind(observer)
        val source = Source.of(observable)
        (1..10).forEach { source.raise(it) }
        assertEquals(
            listOf("2", "4", "6", "8", "10"),
            events
        )
    }
}
