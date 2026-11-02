package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class OptionalSpec :
    FunSpec({
        test("some/filter-keeps-or-drops-the-value") {
            (Optional.some(2).filter { it > 1 }) shouldBe Optional.some(2)
            (Optional.some(2).filter { it > 5 }) shouldBeSameInstanceAs Optional.None
        }

        test("some/as-a-sequence-and-a-string") {
            Optional.some(2).toSequence().toList() shouldBe listOf(2)
            Optional.some(2).toString() shouldBe "Some(2)"
        }

        test("none/stays-none") {
            val none = Optional.none<Int>()
            (none.filter { true }) shouldBeSameInstanceAs Optional.None
            none.toSequence().toList() shouldBe emptyList()
            none.toString() shouldBe "None"
        }

        test("of/a-null-is-none") {
            (Optional.of<Int>(null)) shouldBeSameInstanceAs Optional.None
            Optional.of(1) shouldBe Optional.some(1)
        }
    })
