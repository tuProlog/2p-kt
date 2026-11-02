package it.unibo.tuprolog.ui.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import it.unibo.tuprolog.solve.flags.FlagDomain

class WebIdeFlagInputSpec :
    FunSpec({
        val domain = FlagDomain.IntRange(1, 10)

        test("values within the domain are valid") {
            isValidIntRangeInput("1", domain).shouldBeTrue()
            isValidIntRangeInput("5", domain).shouldBeTrue()
            isValidIntRangeInput("10", domain).shouldBeTrue()
        }

        test("out-of-range values are rejected even though min max are only browser hints") {
            isValidIntRangeInput("0", domain).shouldBeFalse()
            isValidIntRangeInput("11", domain).shouldBeFalse()
            isValidIntRangeInput("-100", domain).shouldBeFalse()
        }

        test("blank, fractional, and non-numeric values are rejected") {
            isValidIntRangeInput("", domain).shouldBeFalse()
            isValidIntRangeInput("3.5", domain).shouldBeFalse()
            isValidIntRangeInput("abc", domain).shouldBeFalse()
        }
    })
