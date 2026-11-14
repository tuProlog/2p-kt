package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct

class PatternDslSpec :
    FunSpec({
        test("invoking-a-variable-builds-a-pattern") {
            logicProgramming {
                val pattern = varOf("P")("a", "X").shouldBeInstanceOf<Pattern>()
                pattern.functor shouldBeSameInstanceAs varOf("P")
                pattern[0] shouldBe Atom.of("a")
                pattern[1] shouldBeSameInstanceAs varOf("X")
            }
        }

        test("pattern-of-plain-values") {
            logicProgramming {
                val pattern = patternOf("P", "a", "X").shouldBeInstanceOf<Pattern>()
                pattern.functor shouldBeSameInstanceAs varOf("P")
                pattern[1] shouldBeSameInstanceAs varOf("X")
            }
        }

        test("invoking-a-string-still-builds-a-struct") {
            logicProgramming { "P"("a").shouldBeInstanceOf<Struct>().functor shouldBe "P" }
        }

        test("metarules") {
            logicProgramming {
                rule { varOf("P")("X") `if` varOf("Q")("X") }.shouldBeInstanceOf<Rule>().head.shouldBeInstanceOf<Pattern>()
                factOf(varOf("P")("a")).shouldBeInstanceOf<Fact>().head.shouldBeInstanceOf<Pattern>()
            }
        }
    })
