package it.unibo.tuprolog.solve.stdlib.primitive
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils
import kotlin.test.Test

internal class StandardGreaterThanTest {
    @Test
    fun computesCorrectResult() {
        TermOrderingUtils.standardOrderGreaterThanTest.forEach { (input, result) ->
            TermOrderingUtils.assertCorrectResponse(TermGreaterThan, input, result)
        }
    }
}