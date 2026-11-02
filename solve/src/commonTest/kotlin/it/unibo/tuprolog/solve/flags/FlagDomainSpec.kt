package it.unibo.tuprolog.solve.flags

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.toTerm

class FlagDomainSpec :
    FunSpec({
        test("IntRange iterates every value in the range, not just the minimum") {
            val domain = FlagDomain.IntRange(1, 3)
            domain.toList() shouldBe listOf(1.toTerm(), 2.toTerm(), 3.toTerm())
        }

        test("IntRange with a single value iterates exactly that value") {
            val domain = FlagDomain.IntRange(5, 5)
            domain.toList() shouldBe listOf(5.toTerm())
        }

        test("IntRange contains every value between its bounds, inclusive") {
            val domain = FlagDomain.IntRange(1, 3)
            (0.toTerm() !in domain).shouldBeTrue()
            (1.toTerm() in domain).shouldBeTrue()
            (2.toTerm() in domain).shouldBeTrue()
            (3.toTerm() in domain).shouldBeTrue()
            (4.toTerm() !in domain).shouldBeTrue()
        }
    })
