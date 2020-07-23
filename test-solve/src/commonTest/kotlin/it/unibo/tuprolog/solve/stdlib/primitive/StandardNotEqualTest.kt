package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.StandardOperatorUtils
import kotlin.test.Ignore
import kotlin.test.Test

internal class StandardNotEqualTest {

    @Ignore
    @Test
    fun computesCorrectResult() {
        StandardOperatorUtils.standardOrderNotEqualTest.forEach { (input, result) ->
            StandardOperatorUtils.assertCorrectResponse(StandardOrderNotEqual, input, result)
        }
    }
}