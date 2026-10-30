package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
            assertEquals(6, sum)
        }

        test("foreach-with-look-ahead-returns-correct-boolean-as-second-parameter") {
            listOf(1).forEachWithLookahead { _, hasNext -> assertFalse(hasNext) }

            listOf(1, 2).forEachWithLookahead { int, hasNext ->
                when (int) {
                    1 -> assertTrue(hasNext)
                    2 -> assertFalse(hasNext)
                }
            }
        }
    })
