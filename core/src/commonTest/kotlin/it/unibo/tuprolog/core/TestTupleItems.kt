package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.collections.List as KtList

class TestTupleItems {
    private val nums: KtList<Term> = (1..100).map { Integer.of(it) }

    @Test
    fun testWellFormed() {
        Tuple.of(nums).run {
            assertEquals(nums, unfoldedList)
            assertEquals(nums, unfoldedArray.toList())
            assertEquals(nums, unfoldedSequence.toList())
            assertEquals(nums, toList())
            assertEquals(nums, toArray().toList())
            assertEquals(nums, toSequence().toList())
            assertEquals(nums, items.toList())
        }
    }
}
