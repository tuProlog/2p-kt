package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.collections.List as KtList

class ListItemsSpec :
    FunSpec({
        val nums: KtList<Term> = (1..100).map { Integer.of(it) }

        test("well-formed") {
            List.of(nums).run {
                val expected: KtList<Term> = nums + EmptyList()
                unfoldedList shouldBe expected
                unfoldedArray.toList() shouldBe expected
                unfoldedSequence.toList() shouldBe expected
                toList() shouldBe nums
                toArray().toList() shouldBe nums
                toSequence().toList() shouldBe nums
                items.toList() shouldBe nums
            }
        }

        test("non-well-formed") {
            List.from(nums, last = Integer.ZERO).run {
                val expected: KtList<Term> = nums + Integer.ZERO
                unfoldedList shouldBe expected
                unfoldedArray.toList() shouldBe expected
                unfoldedSequence.toList() shouldBe expected
                toList() shouldBe expected
                toArray().toList() shouldBe expected
                toSequence().toList() shouldBe expected
                items.toList() shouldBe expected
            }
        }
    })
