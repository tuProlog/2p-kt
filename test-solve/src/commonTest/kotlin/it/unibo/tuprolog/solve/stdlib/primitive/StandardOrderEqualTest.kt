package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils
import kotlin.test.Test

internal class StandardOrderEqualTest {

    @Test
    fun computesCorrectResult() {
        TermOrderingUtils.standardOrderEqualTest.forEach { (input, result) ->
            TermOrderingUtils.assertCorrectResponse(TermSame, input, result)
        }
    }
}
