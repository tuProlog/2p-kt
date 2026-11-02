package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.collections.List as KtList

class TupleItemsSpec :
    FunSpec({
        val nums: KtList<Term> = (1..100).map { Integer.of(it) }

        test("well-formed") {
            Tuple.of(nums).run {
                unfoldedList shouldBe nums
                unfoldedArray.toList() shouldBe nums
                unfoldedSequence.toList() shouldBe nums
                toList() shouldBe nums
                toArray().toList() shouldBe nums
                toSequence().toList() shouldBe nums
                items.toList() shouldBe nums
            }
        }
    })
