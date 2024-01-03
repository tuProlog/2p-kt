package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals

class TermComparisonTest {
    private val base =
        listOf(
            Var.of("A"),
            Var.of("A"),
            Var.of("B"),
            Var.of("C"),
            Var.anonymous(),
            Var.anonymous(),
        ).sortedWith { x, y -> compareStringsLocaleIndependently(x.completeName, y.completeName) } +
            listOf(
                Real.of(1.1),
                Real.of(2.2),
            ).sortedWith { x, y -> x.decimalValue.compareTo(y.decimalValue) } +
            listOf(
                Integer.of(1),
                Integer.of(2),
            ).sortedWith { x, y -> x.intValue.compareTo(y.intValue) } +
            listOf(
                Atom.of(""),
                Atom.of("a"),
                Atom.of("a b"),
                Atom.of("b"),
            ).sortedWith { x, y -> compareStringsLocaleIndependently(x.value, y.value) }

    private val ordered =
        base + base.map { Struct.of("f", it) } + base.map { Struct.of("g", it) }

    private val unordered = ordered.shuffled()

    @Test
    fun testCompareTo() {
        val reordered = unordered.sortedWith { a, b -> a.compareTo(b) }
        assertEquals(ordered, reordered)
    }

    @Test
    fun testDefaultTermComparator() {
        val reordered = unordered.sortedWith(TermComparator.DefaultComparator)
        assertEquals(ordered, reordered)
    }
}
