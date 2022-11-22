package it.unibo.tuprolog.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestLazySet {

    companion object {
        const val N = 1000
    }

    @Test
    fun testEmptyNess() {
        val empty = LazySet(emptySequence<Int>())
        val nonEmpty = LazySet((1 .. N).asSequence().map { it.toString() })

        assertTrue(empty.isEmpty())
        assertFalse(nonEmpty.isEmpty())
    }

    @Test
    fun testEquality() {
        val first = LazySet((N downTo 1).asSequence())
        val second = LazySet((1 .. N).asSequence())

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun testEnumeration() {
        val expected = (1 .. N).toSet()
        val set = LazySet((1 .. N).asSequence())
        val actual = mutableSetOf<Int>()

        for (item in set) {
            actual.add(item)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun testLazyContains() {
        val set = LazySet((1 .. 3).asSequence())
        assertEquals("{ ... }", set.toString())
        assertTrue(1 in set)
        assertEquals("{ 1, ... }", set.toString())
        assertTrue(2 in set)
        assertEquals("{ 1, 2, ... }", set.toString())
        assertTrue(3 in set)
        assertEquals("{ 1, 2, 3 }", set.toString())
        assertFalse(4 in set)
        assertEquals("{ 1, 2, 3 }", set.toString())
    }

    @Test
    fun testFailedContains() {
        val set = LazySet((1 .. 3).asSequence())
        assertEquals("{ ... }", set.toString())
        assertFalse (4 in set)
        assertEquals("{ 1, 2, 3 }", set.toString())
    }

    @Test
    fun testEmptyToString() {
        val set = LazySet(emptySequence<Int>())
        assertEquals("{ }", set.toString())
    }

    @Test
    fun testSize() {
        val set = LazySet((1 .. N).asSequence())
        assertEquals(N, set.size)
    }

    @Test
    fun testContainsAll() {
        val set = LazySet((1 .. N).asSequence())
        val items = (2 until N).toList()
        assertTrue(set.containsAll(items))
    }
}
