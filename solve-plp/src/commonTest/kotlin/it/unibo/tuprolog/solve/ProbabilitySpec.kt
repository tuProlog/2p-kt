package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule

class ProbabilitySpec :
    FunSpec({
        test("example") {
            val aRule = Rule.of(Atom.of("hello"), Atom.of("world"))
            aRule.probability shouldBe DEFAULT_PROBABILITY
            val anotherRule = aRule.setProbability(0.5)
            anotherRule shouldBe aRule
            aRule.probability shouldBe DEFAULT_PROBABILITY
            anotherRule.probability shouldBe 0.5
            val yetAnotherRule = anotherRule.setProbability(1.5)
            yetAnotherRule shouldBe aRule
            yetAnotherRule shouldBe anotherRule
            yetAnotherRule.probability shouldBe 1.0
        }
    })
