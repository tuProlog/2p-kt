package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Test class for Utils.kt functionalities
 *
 * @author Enrico
 */
class UtilsSpec :
    FunSpec({
        test("foreach-with-look-ahead-works-exactly-like-normal-foreach") {
            // should not launch exception
            emptyList<Nothing>().forEachWithLookahead { _, _ -> throw IllegalStateException() }

            var sum = 0
            listOf(1, 2, 3).forEachWithLookahead { int, _ -> sum += int }
            sum shouldBe 6
        }

        test("foreach-with-look-ahead-returns-correct-boolean-as-second-parameter") {
            listOf(1).forEachWithLookahead { _, hasNext -> hasNext.shouldBeFalse() }

            listOf(1, 2).forEachWithLookahead { int, hasNext ->
                when (int) {
                    1 -> hasNext.shouldBeTrue()
                    2 -> hasNext.shouldBeFalse()
                }
            }
        }
    })
