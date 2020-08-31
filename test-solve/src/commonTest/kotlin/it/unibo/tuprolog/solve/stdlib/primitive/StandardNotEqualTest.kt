package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils
import kotlin.test.Test

internal class StandardNotEqualTest {

    @Test
    fun computesCorrectResult() {
        TermOrderingUtils.standardOrderNotEqualTest.forEach { (input, result) ->
            TermOrderingUtils.assertCorrectResponse(TermNotSame, input, result)
        }
    }
}