package it.unibo.tuprolog.solve.stdlib.primitive
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.StandardOperatorUtils
import kotlin.test.Test

internal class StandardGreaterThanTest {
    @Test
    fun computesCorrectResult() {
        StandardOperatorUtils.standardOrderGreaterThanTest.forEach { (input, result) ->
            StandardOperatorUtils.assertCorrectResponse(StandardOrderGreaterThan, input, result)
        }
    }
}