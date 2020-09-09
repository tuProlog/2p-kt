package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.CatchUtils
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Test class for [Catch]
 *
 * @author Enrico
 */
@Ignore
internal class CatchTest {

    @Test
    fun catchWorksLikeCallIfNoErrorIsThrownByFirstArgument() {
        CatchUtils.requestSolutionMap.forEach { (request, solutionList) ->
            val toBeTested = Catch.wrappedImplementation(request).toList()

            assertSolutionEquals(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun catchPrologStandardExamplesWorkAsExpected() {
        CatchUtils.prologStandardCatchExamples.forEach { (request, solutionList) ->
            val toBeTested = Catch.wrappedImplementation(request).toList()

            assertSolutionEquals(solutionList, toBeTested.map { it.solution })
        }
    }
}
