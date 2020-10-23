package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals

class TermComparisonTest {
    private val ordered = orderedTerms

    private val unordered = ordered.shuffled()

    @Test
    fun testCompareTo() {
        val reordered = unordered.sortedWith(Comparator { a, b -> a.compareTo(b) })
        assertEquals(ordered, reordered)
    }

    @Test
    fun testDefaultTermComparator() {
        val reordered = unordered.sortedWith(TermComparator.DefaultComparator)
        assertEquals(ordered, reordered)
    }
}
