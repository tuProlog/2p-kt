package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.collections.List as KtList

class ListItemsSpec :
    FunSpec({
        val nums: KtList<Term> = (1..100).map { Integer.of(it) }

        test("well-formed") {
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

        test("non-well-formed") {
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
    })
