package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.CatchUtils
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.PrimitiveUtils.assertErrorCauseChainComputedCorrectly
import it.unibo.tuprolog.solve.assertSolutionEquals
import kotlin.test.Test

/**
 * Test class for [Catch]
 *
 * @author Enrico
 */
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

    @Test
    fun catchThrowPrologStandardExamplesWorkAsExpected() {
        CatchUtils.prologStandardThrowExamples.forEach { (request, solutionList) ->
            val toBeTested = Catch.wrappedImplementation(request).toList()

            assertSolutionEquals(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun catchErrorChainComputedCorrectly() {
        CatchUtils.prologStandardThrowExamplesWithError.forEach { (request, solutionList) ->
            assertErrorCauseChainComputedCorrectly(request, solutionList.single())
        }
    }
}
