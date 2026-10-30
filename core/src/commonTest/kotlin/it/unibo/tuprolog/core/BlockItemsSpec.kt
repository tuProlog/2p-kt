package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.collections.List as KtList

class BlockItemsSpec :
    FunSpec({
        val nums: KtList<Term> = (1..100).map { Integer.of(it) }

        test("well-formed") {
            Block.of(nums).run {
                assertEquals(nums, unfoldedList)
                assertEquals(nums, unfoldedArray.toList())
                assertEquals(nums, unfoldedSequence.toList())
                assertEquals(nums, toList())
                assertEquals(nums, toArray().toList())
                assertEquals(nums, toSequence().toList())
                assertEquals(nums, items.toList())
            }
        }
    })
