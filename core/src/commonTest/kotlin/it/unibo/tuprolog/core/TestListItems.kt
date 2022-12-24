package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.collections.List as KtList

class TestListItems {
    private val nums: KtList<Term> = (1..100).map { Integer.of(it) }

    @Test
    fun testWellFormed() {
        List.of(nums).run {
            val expected: KtList<Term> = nums + EmptyList()
            assertEquals(expected, unfoldedList)
            assertEquals(expected, unfoldedArray.toList())
            assertEquals(expected, unfoldedSequence.toList())
            assertEquals(nums, toList())
            assertEquals(nums, toArray().toList())
            assertEquals(nums, toSequence().toList())
            assertEquals(nums, items.toList())
        }
    }

    @Test
    fun testNonWellFormed() {
        List.from(nums, last = Integer.ZERO).run {
            val expected: KtList<Term> = nums + Integer.ZERO
            assertEquals(expected, unfoldedList)
            assertEquals(expected, unfoldedArray.toList())
            assertEquals(expected, unfoldedSequence.toList())
            assertEquals(expected, toList())
            assertEquals(expected, toArray().toList())
            assertEquals(expected, toSequence().toList())
            assertEquals(expected, items.toList())
        }
    }
}
