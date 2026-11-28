package it.unibo.tuprolog.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LongIndexedTest {
    @Test
    fun testCompareToWithStandardDeltas() {
        val first = LongIndexed.of(1L, "a")
        val second = LongIndexed.of(2L, "b")
        val same = LongIndexed.of(1L, "c")

        assertTrue(first < second)
        assertTrue(second > first)
        assertEquals(0, first.compareTo(same))
    }

    @Test
    fun testCompareToWithDeltaExceedingIntMax() {
        // Regression test for issue #936:
        // (1L shl 31) vs 0L has a delta of 2^31.
        // Under the old (index - other.index).toInt(), (2^31).toInt() evaluated to Int.MIN_VALUE (< 0),
        // causing a larger index to incorrectly compare as less than 0L.
        val large = LongIndexed.of(1L shl 31, "large")
        val zero = LongIndexed.of(0L, "zero")

        assertTrue(large > zero)
        assertTrue(zero < large)
    }

    @Test
    fun testCompareToWithPowerOfTwoDelta32() {
        // Regression test for issue #936:
        // (1L shl 32) vs 0L has a delta of 2^32.
        // Under the old (index - other.index).toInt(), (2^32).toInt() truncated to 0,
        // causing two distinct indices to incorrectly compare as equal.
        val large = LongIndexed.of(1L shl 32, "large")
        val zero = LongIndexed.of(0L, "zero")

        assertTrue(large > zero)
        assertTrue(zero < large)
    }

    @Test
    fun testCompareToWithExtremeBoundariesDoesNotOverflow() {
        // Subtraction-based comparison (Long.MIN_VALUE - Long.MAX_VALUE) overflows Long,
        // whereas index.compareTo(other.index) handles extreme values correctly.
        val min = LongIndexed.of(Long.MIN_VALUE, "min")
        val max = LongIndexed.of(Long.MAX_VALUE, "max")
        val zero = LongIndexed.of(0L, "zero")

        assertTrue(min < max)
        assertTrue(max > min)
        assertTrue(min < zero)
        assertTrue(max > zero)
    }

    @Test
    fun testCompareToMatchesLongCompareToAcrossValues() {
        val testIndices =
            listOf(
                Long.MIN_VALUE,
                Long.MIN_VALUE + 1,
                -1L shl 32,
                -1L shl 31,
                -1000L,
                -1L,
                0L,
                1L,
                1000L,
                1L shl 31,
                1L shl 32,
                Long.MAX_VALUE - 1,
                Long.MAX_VALUE,
            )

        for (i in testIndices) {
            for (j in testIndices) {
                val indexedI = LongIndexed.of(i, "value")
                val indexedJ = LongIndexed.of(j, "value")
                val expected = i.compareTo(j)
                val actual = indexedI.compareTo(indexedJ)

                if (expected < 0) {
                    assertTrue(actual < 0)
                } else if (expected > 0) {
                    assertTrue(actual > 0)
                } else {
                    assertEquals(0, actual)
                }
            }
        }
    }

    @Test
    fun testSortingWithLargeIndexDeltas() {
        val indices =
            listOf(
                Long.MAX_VALUE,
                0L,
                Long.MIN_VALUE,
                1L shl 32,
                -100L,
                1L shl 31,
                42L,
            )

        val sorted = indices.map { LongIndexed.of(it, it.toString()) }.sorted()
        val expected = indices.sorted().map { LongIndexed.of(it, it.toString()) }

        assertEquals(expected, sorted)
    }

    @Test
    fun testMapPreservesIndexAndOrdering() {
        val item = LongIndexed.of(1L shl 32, 10)
        val mapped = item.map { it * 2 }

        assertEquals(1L shl 32, mapped.index)
        assertEquals(20, mapped.value)

        val smaller = LongIndexed.of(0L, 20)
        assertTrue(mapped > smaller)
    }
}
